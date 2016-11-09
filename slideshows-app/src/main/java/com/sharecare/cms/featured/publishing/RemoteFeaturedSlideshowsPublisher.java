package com.sharecare.cms.featured.publishing;

import com.sharecare.cms.featured.configuration.FeaturedSlideshowsModuleConfig;
import com.sharecare.cms.featured.remote.FeaturedSlideshowsRequestBuilder;
import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;
import com.sharecare.cms.publishing.commons.activation.RemoteServiceResponseProcessor;
import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import com.sharecare.cms.publishing.commons.configuration.RemoteServerResourceConfig;
import com.sharecare.core.sdk.BasicResponse;
import com.sharecare.core.sdk.configuration.BasicAuthCredentials;
import com.sharecare.core.sdk.configuration.ServerInfo;
import com.sharecare.sdk.slideshows.featured.FeaturedSlideshowsApiClient;
import com.sharecare.sdk.slideshows.featured.model.FeaturedSlideshowRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class RemoteFeaturedSlideshowsPublisher implements RemoteDataPublisher {

    static final String NODE_TYPE = "mgnl:featured";
    private final Map<String, FeaturedSlideshowsApiClient> clientMap;
    private final FeaturedSlideshowsRequestBuilder featuredSlideshowsRequestBuilder;
    private final RemoteServiceResponseProcessor remoteServiceResponseProcessor;

    @Inject
    public RemoteFeaturedSlideshowsPublisher(FeaturedSlideshowsModuleConfig featuredSlideshowsModuleConfig,
                                             CommonsModuleConfig commonsModuleConfig,
                                             FeaturedSlideshowsRequestBuilder featuredSlideshowsRequestBuilder,
                                             RemoteServiceResponseProcessor remoteServiceResponseProcessor) {
        this.featuredSlideshowsRequestBuilder = featuredSlideshowsRequestBuilder;
        this.remoteServiceResponseProcessor = remoteServiceResponseProcessor;
        this.clientMap = buildApiClients(featuredSlideshowsModuleConfig.getPublishing()
                .get(commonsModuleConfig.getEnvironment()));

    }

    @Override
    public boolean publish(Node node, String environment) {
        try {
            log.info("Publishing {}:{} content to {} ", node.getName(), node.getIdentifier(), environment);
            FeaturedSlideshowsApiClient client = clientMap.get(environment);
            FeaturedSlideshowRequest featuredSlideshowRequest = featuredSlideshowsRequestBuilder.forNode(node);
            BasicResponse response = client.saveRequest().withData(featuredSlideshowRequest).execute();
            return remoteServiceResponseProcessor.processResponse(node, environment, response,
                    RemoteServiceResponseProcessor.addEnvironmentCallback);
        } catch (RepositoryException e) {
            log.error("Failed activation of featured slideshow  {} ", ExceptionUtils.getFullStackTrace(e));
            return false;
        }
    }

    @Override
    public boolean unPublish(Node node, String environment) {
        throw new RuntimeException("Deactivation is not available for Featured Slideshows");
    }

    private Map<String, FeaturedSlideshowsApiClient> buildApiClients(Map<String, RemoteServerResourceConfig> environmentsMap) {

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
                                    .basePath("/slideshows/featured")
                                    .build();
                            return new FeaturedSlideshowsApiClient(serverInfo, basicAuthCredentials);
                        }));
    }


    @Override
    public boolean canService(String nodeType) {
        return NODE_TYPE.equals(nodeType);
    }
}
