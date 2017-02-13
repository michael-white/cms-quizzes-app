package com.sharecare.cms.quizzes.fieldvalidator;

import com.google.inject.Inject;
import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import com.sharecare.cms.publishing.commons.configuration.RemoteServerResourceConfig;
import com.sharecare.cms.quizzes.configuration.QuizzesModuleConfig;
import com.sharecare.core.sdk.DataResponse;
import com.sharecare.core.sdk.configuration.BasicAuthCredentials;
import com.sharecare.core.sdk.configuration.ServerInfo;
import com.sharecare.quizzes.sdk.QuizzesApiClient;
import com.sharecare.quizzes.sdk.model.FullQuizResponse;
import com.sharecare.quizzes.sdk.model.QuizResponse;
import org.apache.commons.lang.StringUtils;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class ResourceUriDuplicateFieldValidator implements com.vaadin.data.Validator {

    private Map<String,QuizzesApiClient> clientMap;
    private final String environment;
    private final static String ENDPOINT_BASEPATH = "/quizzes";

    @Inject
    public ResourceUriDuplicateFieldValidator(QuizzesModuleConfig moduleConfig,
                                              CommonsModuleConfig commonsModuleConfig){
        this.environment = commonsModuleConfig.getEnvironment();
    }

    @Override
    public void validate(Object value) throws InvalidValueException {

        if(value == null || StringUtils.isBlank(value.toString())){
            throw new InvalidValueException("Quiz Resource URI is empty");
        }



        try {
            QuizzesApiClient client = clientMap.get(environment);
            DataResponse<FullQuizResponse> searchResult = client.getRequest()
                    .withUri(value.toString())
                    .execute();

            if( null != searchResult.getResult() ){
                throw new InvalidValueException("Quiz Resource URI : " + value + " already exists");
            }

        }catch (Exception ex){
            throw new InvalidValueException("Quiz Resource URI error");
        }
    }

    private Map<String, QuizzesApiClient> buildApiClients(Map<String, RemoteServerResourceConfig> environmentsMap) {

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
                            return new QuizzesApiClient(basicAuthCredentials, serverInfo);
                        }));
    }
}