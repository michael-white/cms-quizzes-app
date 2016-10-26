package com.sharecare.cms.slideshows.activation.publishing;

import com.sharecare.cms.cloudinary.dam.AssetUploadResult;
import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;
import com.sharecare.cms.publishing.commons.activation.RemoteServiceResponseProcessor;
import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import com.sharecare.cms.publishing.commons.configuration.RemoteServerResourceConfig;
import com.sharecare.cms.slideshows.activation.remote.SlideshowsAssetProcessor;
import com.sharecare.cms.slideshows.activation.remote.SlideshowsRequestBuilder;
import com.sharecare.cms.slideshows.configuration.SlideshowsModuleConfig;
import com.sharecare.core.sdk.BasicResponse;
import com.sharecare.core.sdk.configuration.BasicAuthCredentials;
import com.sharecare.core.sdk.configuration.ServerInfo;
import com.sharecare.slideshows.sdk.SlideshowsApiClient;
import com.sharecare.slideshows.sdk.model.SlideshowRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.inject.Inject;
import javax.jcr.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
class RemoteSlideshowsPublisher implements RemoteDataPublisher {

    static final String NODE_TYPE = "mgnl:slideshow";
    private final Map<String, SlideshowsApiClient> clientMap;
    private final SlideshowsRequestBuilder slideshowRequestBuilder;
    private final RemoteServiceResponseProcessor remoteServiceResponseProcessor;

    @Inject
    public RemoteSlideshowsPublisher(SlideshowsModuleConfig slideshowsModuleConfig,
                                     CommonsModuleConfig commonsModuleConfig, SlideshowsRequestBuilder slideshowRequestBuilder, RemoteServiceResponseProcessor remoteServiceResponseProcessor) {
        this.slideshowRequestBuilder = slideshowRequestBuilder;
        this.remoteServiceResponseProcessor = remoteServiceResponseProcessor;
        this.clientMap = buildApiClients(slideshowsModuleConfig.getPublishing().get(commonsModuleConfig.getEnvironment()));

    }

    @Override
    public boolean publish(Node node, String environment) {
        try {
            log.info("Publishing {}:{} content to {} ", node.getName(), node.getIdentifier(), environment);
            SlideshowsApiClient client = clientMap.get(environment);
            List<SlideshowRequest> slideshowRequests = slideshowRequestBuilder.forNode(node);
            BasicResponse response = client.saveRequest().withData(slideshowRequests).execute();
            return remoteServiceResponseProcessor.processResponse(node, environment, response);
        } catch (RepositoryException e) {
            log.error("Failed Activation of slideshow  {} ", ExceptionUtils.getFullStackTrace(e));
            return false;
        }
    }

    @Override
    public boolean unPublish(Node node, String environment) {

        try {
            log.warn("Deleting {}:{} content from {} ", node.getName(), node.getIdentifier(), environment);
            SlideshowsApiClient client = clientMap.get(environment);
            log.debug("Executing DELETE rest call {}", node.getName());
            BasicResponse response = client.deleteRequest().withUri(node.getName()).execute();
            return remoteServiceResponseProcessor.processResponse(node, environment, response);
        } catch (Exception e) {
            log.error("Failed De-Activation of slideshow  {} ", ExceptionUtils.getFullStackTrace(e));
            return false;
        }
    }

    private Map<String, SlideshowsApiClient> buildApiClients(Map<String, RemoteServerResourceConfig> environmentsMap) {

        return environmentsMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            RemoteServerResourceConfig config = entry.getValue();
                            BasicAuthCredentials basicAuthCredentials = new BasicAuthCredentials(entry.getValue().getUsername(), config.getPassword());
                            ServerInfo serverInfo = ServerInfo.builder().protocol(config.getHostProtocol())
                                    .hostName(config.getHostAddress())
                                    .port(config.getHostPort())
                                    .basePath("/slideshows")
                                    .build();
                            return new SlideshowsApiClient(serverInfo, basicAuthCredentials);
                        }));
    }


    @Override
    public boolean canService(String nodeType) {
        return NODE_TYPE.equals(nodeType);
    }
}
