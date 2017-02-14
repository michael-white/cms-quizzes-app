package com.sharecare.cms.featured.activation.publishing;

import com.sharecare.cms.featured.activation.remote.FeaturedQuizzesRequestBuilder;
import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;
import com.sharecare.cms.publishing.commons.activation.RemoteServiceResponseProcessor;
import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import com.sharecare.cms.publishing.commons.configuration.RemoteServerResourceConfig;
import com.sharecare.cms.quizzes.configuration.QuizzesModuleConfig;
import com.sharecare.core.sdk.BasicResponse;
import com.sharecare.core.sdk.configuration.BasicAuthCredentials;
import com.sharecare.core.sdk.configuration.ServerInfo;
import com.sharecare.quizzes.sdk.featured.FeaturedQuizzesApiClient;
import com.sharecare.quizzes.sdk.featured.model.FeaturedQuizRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RemoteFeaturedQuizzesPublisher implements RemoteDataPublisher {

    static final String ROOT_NODE = "mgnl:featuredQuiz";
    private final Map<String, FeaturedQuizzesApiClient> clientMap;
    private final FeaturedQuizzesRequestBuilder featuredQuizzesRequestBuilder;
    private final RemoteServiceResponseProcessor remoteServiceResponseProcessor;

    @Inject
    public RemoteFeaturedQuizzesPublisher(QuizzesModuleConfig quizzesModuleConfig,
                                             CommonsModuleConfig commonsModuleConfig,
                                             FeaturedQuizzesRequestBuilder featuredQuizzesRequestBuilder,
                                             RemoteServiceResponseProcessor remoteServiceResponseProcessor) {
        this.featuredQuizzesRequestBuilder = featuredQuizzesRequestBuilder;
        this.remoteServiceResponseProcessor = remoteServiceResponseProcessor;
        this.clientMap = buildApiClients(quizzesModuleConfig.getPublishing()
                .get(commonsModuleConfig.getEnvironment()));

    }

    @Override
    public boolean publish(Node node, String environment) {
        try {
            log.info("Publishing {}:{} content to {} ", node.getName(), node.getIdentifier(), environment);
            FeaturedQuizzesApiClient client = clientMap.get(environment);
            FeaturedQuizRequest featuredQuizRequest = featuredQuizzesRequestBuilder.forNode(node);
            BasicResponse response = client.saveRequest().withData(featuredQuizRequest).execute();
            return remoteServiceResponseProcessor.processResponse(node, environment, response,
                    RemoteServiceResponseProcessor.addEnvironmentCallback);
        } catch (RepositoryException e) {
            log.error("Failed activation of featured quizzes  {} ", ExceptionUtils.getFullStackTrace(e));
            return false;
        }
    }

    @Override
    public boolean unPublish(Node node, String environment) {
        throw new RuntimeException("Deactivation is not available for Featured Quizzes");
    }

    private Map<String, FeaturedQuizzesApiClient> buildApiClients(Map<String, RemoteServerResourceConfig> environmentsMap) {

        return environmentsMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            RemoteServerResourceConfig config = entry.getValue();
                            BasicAuthCredentials basicAuthCredentials = new BasicAuthCredentials(entry.getValue()
                                    .getUsername(), config.getPassword());
                            ServerInfo serverInfo = ServerInfo.builder().protocol(config.getHostProtocol())
                                    .hostName(config.getHostAddress())
                                    .port(config.getHostPort())
                                    .basePath("/quizzes/featured")
                                    .build();
                            return new FeaturedQuizzesApiClient(basicAuthCredentials, serverInfo);
                        }));
    }


    @Override
    public boolean canService(String nodeType) {
        return ROOT_NODE.equals(nodeType);
    }
}
