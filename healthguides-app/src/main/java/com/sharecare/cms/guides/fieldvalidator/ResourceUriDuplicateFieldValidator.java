package com.sharecare.cms.guides.fieldvalidator;

import com.google.inject.Inject;
import com.sharecare.cms.guides.configuration.HealthGuideModuleConfig;
import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import com.sharecare.cms.publishing.commons.configuration.RemoteServerResourceConfig;
import com.sharecare.core.sdk.DataResponse;
import com.sharecare.core.sdk.configuration.BasicAuthCredentials;
import com.sharecare.core.sdk.configuration.ServerInfo;
import com.sharecare.healthguides.sdk.HealthGuidesApiClient;
import com.sharecare.healthguides.sdk.model.HealthGuideResponse;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by robert.davis on 12/21/2016.
 */
public class ResourceUriDuplicateFieldValidator implements com.vaadin.data.Validator {

    private Map<String,HealthGuidesApiClient> clientMap;
    private final String environment;
    private final static String ENDPOINT_BASEPATH = "/health-guides";

    @Inject
    public ResourceUriDuplicateFieldValidator(HealthGuideModuleConfig moduleConfig,
                                              CommonsModuleConfig commonsModuleConfig){

        this.clientMap = buildApiClients(moduleConfig.getPublishing().get(commonsModuleConfig.getEnvironment()));
        this.environment = commonsModuleConfig.getEnvironment();
    }


    @Override
    public void validate(Object value) throws InvalidValueException {

        if(value == null || StringUtils.isBlank(value.toString())){
            throw new InvalidValueException("Health Guide Resource URI is empty");
        }



        try {
            HealthGuidesApiClient client = clientMap.get(environment);
            DataResponse<Collection<HealthGuideResponse>> searchResult = client.searchRequest()
                    .searchParam("contentRefList.contentUrl", value.toString())
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
                            BasicAuthCredentials basicAuthCredentials = new BasicAuthCredentials(entry.getValue().getUsername(), config.getPassword());
                            ServerInfo serverInfo = ServerInfo.builder().protocol(config.getHostProtocol())
                                    .hostName(config.getHostAddress())
                                    .port(config.getHostPort())
                                    .basePath(ENDPOINT_BASEPATH)
                                    .build();
                            return new HealthGuidesApiClient(basicAuthCredentials, serverInfo);
                        }));
    }
}
