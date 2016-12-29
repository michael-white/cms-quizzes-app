package com.sharecare.cms.guides.activation.remote;

import com.sharecare.cms.cloudinary.dam.AssetUploadResult;
import com.sharecare.cms.guides.schema.HealthGuideJCRSchema;
import com.sharecare.healthguides.sdk.model.HealthGuideRequest;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Optional;

import static com.sharecare.cms.publishing.commons.utils.NodeUtils.*;


public class RemoteHealthGuideRequestBuilder implements HealthGuideRequestBuilder {

    private HealthGuideAssetProcessor healthGuideAssetProcessor;

    @Inject
    public RemoteHealthGuideRequestBuilder(HealthGuideAssetProcessor healthGuideAssetProcessor) {
        this.healthGuideAssetProcessor = healthGuideAssetProcessor;
    }

    @Override
    public HealthGuideRequest forNode(Node node) throws RepositoryException {

        Optional<AssetUploadResult> uploadResult = healthGuideAssetProcessor.uploadAssetFrom(node);

        return HealthGuideRequest.builder()
                .id(node.getIdentifier())
                .healthGuideUri(node.getName())
                .title(fromNode(HealthGuideJCRSchema.title.name(), node))
                .description(fromNode(HealthGuideJCRSchema.description.name(), node))
                .keywords(extractMultiField(node, HealthGuideJCRSchema.keywords.name()))
                .segments(extractMultiField(node, HealthGuideJCRSchema.segments.name()))
                .topicUri(fromNode(HealthGuideJCRSchema.topicUri.name(), node))
                .primaryTag(extractTag(HealthGuideJCRSchema.primaryTag.name(), node))
                .tocImageUrl(uploadResult.isPresent() ? uploadResult.get().getUrl() : "")
                .build();
    }
}
