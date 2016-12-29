package com.sharecare.cms.guides.fieldvalidator;

import com.google.inject.Inject;
import com.sharecare.cms.ClientDelegate;
import com.sharecare.cms.guides.configuration.HealthGuideModuleConfig;
import com.sharecare.cms.guides.remote.HealthGuideRequestBuilder;
import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import com.sharecare.cms.publishing.commons.configuration.RemoteServerResourceConfig;
import com.sharecare.core.sdk.configuration.BasicAuthCredentials;
import com.sharecare.core.sdk.configuration.ServerInfo;
import com.sharecare.healthguides.sdk.DataResponse;
import com.sharecare.healthguides.sdk.HealthGuidesApiClient;
import com.sharecare.healthguides.sdk.SearchRequest;
import com.sharecare.healthguides.sdk.model.BasicHealthGuideResponse;
import com.sharecare.healthguides.sdk.model.Identifier;
import com.vaadin.data.Validator;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by robert.davis on 12/21/2016.
 */
public class ResourceUriDuplicateFieldValidator implements com.vaadin.data.Validator {

    private Map<String,HealthGuidesApiClient> clientMap;
    private HealthGuideRequestBuilder requestBuilder;
    private final String environment;

    @Inject
    public ResourceUriDuplicateFieldValidator(HealthGuideModuleConfig moduleConfig,
                                              CommonsModuleConfig commonsModuleConfig,
                                              HealthGuideRequestBuilder requestBuilder){

        this.clientMap = buildApiClients(moduleConfig.getPublishing().get(commonsModuleConfig.getEnvironment()));
        this.requestBuilder = requestBuilder;
        this.environment = commonsModuleConfig.getEnvironment();
    }


    @Override
    public void validate(Object value) throws InvalidValueException {

        if(value == null || StringUtils.isBlank(value.toString())){
            throw new InvalidValueException("Health Guide Resource URI is empty");
        }

        HealthGuidesApiClient client = clientMap.get(environment);

        try {
            DataResponse<Collection<BasicHealthGuideResponse>> searchResult = client.searchRequest()
                                                                                    .searchParam("contentRefList", value.toString())
                                                                                    .execute();

            if( ! searchResult.getResult().isEmpty() ){
                throw new InvalidValueException("Health Guide Resource URI : " + value + " already exists");
            }

        }catch (Exception ex){
            throw new InvalidValueException("Health Guide Resource URI error");
        }
    }

    private Map<String, HealthGuidesApiClient> buildApiClients(Map<String, RemoteServerResourceConfig> environmentsMap) {

        return environmentsMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            RemoteServerResourceConfig config = entry.getValue();
                            com.sharecare.healthguides.sdk.configuration.BasicAuthCredentials basicAuthCredentials = new com.sharecare.healthguides.sdk.configuration.BasicAuthCredentials(entry.getValue().getUsername(), config.getPassword());
                            com.sharecare.healthguides.sdk.configuration.ServerInfo serverInfo = com.sharecare.healthguides.sdk.configuration.ServerInfo.builder().protocol(config.getHostProtocol())
                                    .hostName(config.getHostAddress())
                                    .port(config.getHostPort())
                                    .basePath("/health-guides")
                                    .build();
                            return new HealthGuidesApiClient(basicAuthCredentials, serverInfo);
                        }));
    }
}
