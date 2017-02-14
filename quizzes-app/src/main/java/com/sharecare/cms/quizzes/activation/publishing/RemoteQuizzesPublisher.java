package com.sharecare.cms.quizzes.activation.publishing;

import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;
import com.sharecare.cms.publishing.commons.activation.RemoteServiceResponseProcessor;
import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import com.sharecare.cms.publishing.commons.configuration.RemoteServerResourceConfig;
import com.sharecare.cms.quizzes.activation.remote.QuizzesRequestBuilder;
import com.sharecare.cms.quizzes.configuration.QuizzesModuleConfig;
import com.sharecare.core.sdk.BasicResponse;
import com.sharecare.core.sdk.configuration.BasicAuthCredentials;
import com.sharecare.core.sdk.configuration.ServerInfo;
import com.sharecare.quizzes.sdk.QuizzesApiClient;
import com.sharecare.quizzes.sdk.model.QuizRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
class RemoteQuizzesPublisher implements RemoteDataPublisher {

    static final String NODE_TYPE = "mgnl:quiz";
    private final Map<String, QuizzesApiClient> clientMap;
    private final QuizzesRequestBuilder quizRequestBuilder;
    private final RemoteServiceResponseProcessor remoteServiceResponseProcessor;

    @Inject
    public RemoteQuizzesPublisher(QuizzesModuleConfig quizzesModuleConfig,
                                     CommonsModuleConfig commonsModuleConfig,
                                     QuizzesRequestBuilder quizRequestBuilder,
                                     RemoteServiceResponseProcessor remoteServiceResponseProcessor) {
        this.quizRequestBuilder = quizRequestBuilder;
        this.remoteServiceResponseProcessor = remoteServiceResponseProcessor;
        this.clientMap = buildApiClients(quizzesModuleConfig.getPublishing()
                .get(commonsModuleConfig.getEnvironment()));

    }

    @Override
    public boolean publish(Node node, String environment) {
        try {
            log.info("Publishing {}:{} content to {} ", node.getName(), node.getIdentifier(), environment);
            QuizzesApiClient client = clientMap.get(environment);
            QuizRequest quizRequest = quizRequestBuilder.forNode(node, environment);
            BasicResponse response = client.putRequest().withData(quizRequest)
                                                        .withUri(quizRequest.getId())
                                                        .execute();
            return remoteServiceResponseProcessor.processResponse(node, environment, response,
                    RemoteServiceResponseProcessor.addEnvironmentCallback);
        } catch (RepositoryException e) {
            log.error("Failed Activation of quiz  {} ", ExceptionUtils.getFullStackTrace(e));
            return false;
        }
    }

    @Override
    public boolean unPublish(Node node, String environment) {

        try {
            log.warn("Deleting {}:{} content from {} ", node.getName(), node.getIdentifier(), environment);
            QuizzesApiClient client = clientMap.get(environment);
            log.debug("Executing DELETE rest call {}", node.getName());
            BasicResponse response = client.deleteRequest().withUri(node.getIdentifier())
                                                           .execute();
            return remoteServiceResponseProcessor.processResponse(node, environment, response,
                    RemoteServiceResponseProcessor.removeEnvironmentCallback);
        } catch (Exception e) {
            log.error("Failed De-Activation of quiz  {} ", ExceptionUtils.getFullStackTrace(e));
            return false;
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
                                    .basePath("/quizzes")
                                    .build();
                            return new QuizzesApiClient(basicAuthCredentials, serverInfo);
                        }));
    }


    @Override
    public boolean canService(String nodeType) {
        return NODE_TYPE.equals(nodeType);
    }
}
