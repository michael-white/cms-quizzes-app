package com.sharecare.cms.articles.activation.publishing;

import com.sharecare.articles.sdk.ArticlesApiClient;
import com.sharecare.articles.sdk.model.ArticleRequest;
import com.sharecare.cms.articles.activation.remote.ArticleAssetProcessor;
import com.sharecare.cms.articles.activation.remote.ArticleRequestBuilder;
import com.sharecare.cms.articles.configuration.ArticlesModuleConfig;
import com.sharecare.cms.cloudinary.dam.AssetUploadResult;
import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;
import com.sharecare.cms.publishing.commons.activation.RemoteServiceResponseProcessor;
import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import com.sharecare.cms.publishing.commons.configuration.RemoteServerResourceConfig;
import com.sharecare.core.sdk.BasicResponse;
import com.sharecare.core.sdk.configuration.BasicAuthCredentials;
import com.sharecare.core.sdk.configuration.ServerInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
class RemoteArticlePublisher implements RemoteDataPublisher {

    static final String NODE_TYPE = "mgnl:article";

    private final Map<String, ArticlesApiClient> clientMap;
    private final ArticleRequestBuilder articleRequestBuilder;
    private final ArticleAssetProcessor articleAssetProcessor;
    private final RemoteServiceResponseProcessor remoteServiceResponseProcessor;


    @Inject
    public RemoteArticlePublisher(ArticlesModuleConfig articlesModuleConfig,
                                  CommonsModuleConfig commonsModuleConfig,
                                  ArticleRequestBuilder articleRequestBuilder,
                                  ArticleAssetProcessor articleAssetProcessor,
                                  RemoteServiceResponseProcessor remoteServiceResponseProcessor) {
        this.articleAssetProcessor = articleAssetProcessor;
        this.remoteServiceResponseProcessor = remoteServiceResponseProcessor;
        this.clientMap = buildApiClients(articlesModuleConfig.getPublishing().get(commonsModuleConfig.getEnvironment()));
        this.articleRequestBuilder = articleRequestBuilder;
    }

    @Override
    public boolean publish(Node node, String environment) {

        try {
            log.info("Publishing {}:{} content to {} ", node.getName(), node.getIdentifier(), environment);
            ArticlesApiClient client = clientMap.get(environment);
            Optional<AssetUploadResult> uploadResult = articleAssetProcessor.uploadAssetFrom(node);
            List<ArticleRequest> articleRequests = articleRequestBuilder.forNode(node, uploadResult);

            BasicResponse response = client.saveRequest().withData(articleRequests).execute();
            return remoteServiceResponseProcessor.processResponse(node, environment, response);
        } catch (Exception e) {
            log.error("Failed Activation of article  {} ", ExceptionUtils.getFullStackTrace(e));
            return false;
        }
    }

    @Override
    public boolean unPublish(Node node, String environment) {

        try {
            log.warn("Deleting {}:{} content from {} ", node.getName(), node.getIdentifier(), environment);
            ArticlesApiClient client = clientMap.get(environment);
            log.debug("Executing DELETE rest call {}", node.getName());
            BasicResponse response = client.deleteRequest().withUri(node.getName()).execute();
            return remoteServiceResponseProcessor.processResponse(node, environment, response);
        } catch (Exception e) {
            log.error("Failed De-Activation of article  {} ", ExceptionUtils.getFullStackTrace(e));
            return false;
        }
    }

    private Map<String, ArticlesApiClient> buildApiClients(Map<String, RemoteServerResourceConfig> environmentsMap) {

        return environmentsMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            RemoteServerResourceConfig config = entry.getValue();
                            BasicAuthCredentials basicAuthCredentials = new BasicAuthCredentials(entry.getValue().getUsername(), config.getPassword());
                            ServerInfo serverInfo = ServerInfo.builder().protocol(config.getHostProtocol())
                                    .hostName(config.getHostAddress())
                                    .port(config.getHostPort())
                                    .basePath("/articles")
                                    .build();
                            return new ArticlesApiClient(serverInfo, basicAuthCredentials);
                        }));
    }

    @Override
    public boolean canService(String nodeType) {
        return NODE_TYPE.equals(nodeType);
    }
}
