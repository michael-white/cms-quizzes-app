package com.sharecare.cms.guides.activation.remote;

import com.sharecare.articles.sdk.ArticlesApiClient;
import com.sharecare.cms.cloudinary.dam.AssetUploadResult;
import com.sharecare.cms.guides.configuration.HealthGuideModuleConfig;
import com.sharecare.cms.guides.schema.HealthGuideJCRSchema;
import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import com.sharecare.cms.publishing.commons.configuration.RemoteServerResourceConfig;
import com.sharecare.core.sdk.configuration.BasicAuthCredentials;
import com.sharecare.core.sdk.configuration.ServerInfo;
import com.sharecare.healthguides.sdk.model.ContentRefRequest;
import com.sharecare.healthguides.sdk.model.HealthGuideRequest;
import com.sharecare.healthguides.sdk.model.OpenGraph;
import com.sharecare.healthguides.sdk.model.Sponsor;
import com.sharecare.sdk.slideshows.SlideshowsApiClient;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.*;
import java.util.stream.Collectors;

import static com.sharecare.cms.publishing.commons.utils.NodeUtils.*;

@Slf4j
public class RemoteHealthGuideRequestBuilder implements HealthGuideRequestBuilder {

    private final Map<String, ArticlesApiClient> articleClientMap;
    private final HealthGuideAssetProcessor healthGuideAssetProcessor;
    private final Map<String, SlideshowsApiClient> slideshowClientMap;


    @Inject
    public RemoteHealthGuideRequestBuilder(CommonsModuleConfig commonsModuleConfig,
                                           HealthGuideModuleConfig healthGuideModuleConfig,
                                           HealthGuideAssetProcessor healthGuideAssetProcessor) {
        this.healthGuideAssetProcessor = healthGuideAssetProcessor;
        this.articleClientMap = buildArticleApiClients(healthGuideModuleConfig.getPublishing().get(commonsModuleConfig.getEnvironment()));
        this.slideshowClientMap = buildSlideshowApiClients(healthGuideModuleConfig.getPublishing().get(commonsModuleConfig.getEnvironment()));

    }

    @Override
    public HealthGuideRequest forNode(Node node, String environment) throws RepositoryException {

        Optional<AssetUploadResult> uploadResult = healthGuideAssetProcessor.uploadAssetFrom(node);

        Sponsor sponsor = Sponsor.builder()
                .sponsorName(fromNode(HealthGuideJCRSchema.sponsorName.name(), node))
                .build();

        OpenGraph openGraph = OpenGraph.builder()
                .description(fromNode(HealthGuideJCRSchema.ogDescription.name(), node))
                .image(fromNode(HealthGuideJCRSchema.ogImage.name(), node))
                .label(fromNode(HealthGuideJCRSchema.ogLabel.name(), node))
                .title(fromNode(HealthGuideJCRSchema.ogTitle.name(), node))
                .type(fromNode(HealthGuideJCRSchema.ogTitle.name(), node))
                .url(fromNode(HealthGuideJCRSchema.ogUrl.name(), node))
                .build();

        return HealthGuideRequest.builder()
                .id(node.getIdentifier())
                .healthGuideUri(node.getName())
                .contentRefList(extractContentRefList(node, environment))
                .title(fromNode(HealthGuideJCRSchema.title.name(), node))
                .description(fromNode(HealthGuideJCRSchema.description.name(), node))
                .keywords(extractMultiField(node, HealthGuideJCRSchema.keywords.name()))
                .segments(extractMultiField(node, HealthGuideJCRSchema.segments.name()))
                .topicUri(fromNode(HealthGuideJCRSchema.topicUri.name(), node))
                .primaryTag(extractTag(HealthGuideJCRSchema.primaryTag.name(), node))
                .sponsor(sponsor)
                .openGraph(openGraph)
                .pageAndMetaTitle(fromNode(HealthGuideJCRSchema.pageAndMetaTitle.name(), node))
                .disableSocial(Boolean.valueOf(fromNode(HealthGuideJCRSchema.disableSocial.name(), node)))
                .noIndexFollow(Boolean.valueOf(fromNode(HealthGuideJCRSchema.noIndexFollow.name(), node)))
                .canonicalReference(fromNode(HealthGuideJCRSchema.canonicalReference.name(), node))
                .metaDescription(fromNode(HealthGuideJCRSchema.metaDescription.name(), node))
                .tocImageUrl(uploadResult.isPresent() ? uploadResult.get().getUrl() : "")
                .build();
    }

    private Collection<ContentRefRequest> extractContentRefList(Node node, String environment) {
        List<ContentRefRequest> contentRefRequests = new ArrayList<>();
        try {
            int order = 0;
            NodeIterator contentRefListIterator = node.getNode(HealthGuideJCRSchema.contentRefList.name()).getNodes();
            while (contentRefListIterator.hasNext()) {
                Node contentNode = contentRefListIterator.nextNode();
                String uri = contentNode.getProperty("resourceUri").getValue().getString();
                String type = contentNode.getProperty("type").getValue().getString().toLowerCase();
                contentRefRequests.add(ContentRefRequest.builder()
                                .id(getIdForContentEnvironment(uri, type, environment))
                                .type(type)
                                .order(order)
                                .build()
                );
                order++;
            }
        } catch (Exception e) {
            log.error("Error occurred while trying to iterate through contentRefList", e);
        }
        return contentRefRequests;
    }

    private String getIdForContentEnvironment(String uri, String type, String environment) {
        String id;
        switch (type) {
            case "article":
                ArticlesApiClient articlesApiClient = articleClientMap.get(environment);
                id = articlesApiClient.getRequest()
                        .withUri(uri)
                        .execute()
                        .getResult()
                        .getId();
                break;
            case "slideshow":
                SlideshowsApiClient slideshowsApiClient = slideshowClientMap.get(environment);
                id = slideshowsApiClient.getRequest()
                        .withUri(uri)
                        .execute()
                        .getResult()
                        .getId();
                break;
            default:
                throw new RuntimeException();
        }

        return id;
    }

    private Map<String, ArticlesApiClient> buildArticleApiClients(Map<String, RemoteServerResourceConfig> environmentsMap) {

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

    private Map<String, SlideshowsApiClient> buildSlideshowApiClients(Map<String, RemoteServerResourceConfig> environmentsMap) {

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
                                    .basePath("/slideshows")
                                    .build();
                            return new SlideshowsApiClient(serverInfo, basicAuthCredentials);
                        }));
    }
}
