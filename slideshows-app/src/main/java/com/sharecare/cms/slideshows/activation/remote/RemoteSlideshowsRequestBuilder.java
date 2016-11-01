package com.sharecare.cms.slideshows.activation.remote;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sharecare.cms.cloudinary.dam.AssetUploadResult;
import com.sharecare.cms.slideshows.schema.SlideshowsJCRSchema;
import com.sharecare.core.sdk.model.Tag;
import com.sharecare.slideshows.sdk.model.SlideRequest;
import com.sharecare.slideshows.sdk.model.SlideshowRequest;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.*;
import java.util.*;

@Slf4j
public class RemoteSlideshowsRequestBuilder implements SlideshowsRequestBuilder {

    private final SlideshowsAssetProcessor slideshowsAssetProcessor;

    @Inject
    public RemoteSlideshowsRequestBuilder(SlideshowsAssetProcessor slideshowsAssetProcessor) {
        this.slideshowsAssetProcessor = slideshowsAssetProcessor;
    }

    @Override
    public List<SlideshowRequest> forNode(Node node) throws RepositoryException {

        String uuid = node.hasNode(SlideshowsJCRSchema.legacyUUID.name()) ? node.getProperty(SlideshowsJCRSchema.legacyUUID.name()).getString() : node.getIdentifier();

        SlideshowRequest request = SlideshowRequest.builder()
                .id(uuid)
                .uri(node.getName())
                .title(fromNode(SlideshowsJCRSchema.title.name(), node))
                .description(fromNode(SlideshowsJCRSchema.description.name(), node))
                .slides(processSlides(node))
                .pageAndMetaTitle(fromNode(SlideshowsJCRSchema.pageAndMetaTitle.name(), node))
                .keywords(fromCSV(fromNode(SlideshowsJCRSchema.metaKeywords.name(), node)))
                .disableSocial(Boolean.valueOf(fromNode(SlideshowsJCRSchema.disableSocial.name(), node)))
                .segments(extractMultiField(node, SlideshowsJCRSchema.segmentSelect.name()))
                .authors(extractMultiField(node, SlideshowsJCRSchema.authors.name()))
                .mentions(extractMultiField(node, SlideshowsJCRSchema.mentions.name()))
                .topicUri(fromNode(SlideshowsJCRSchema.topicUri.name(), node))
                .primaryTag(extractTag(SlideshowsJCRSchema.primaryTag.name(), node))
                .legacyUris(extractMultiField(node, SlideshowsJCRSchema.redirects.name()))
                .build();


        return Lists.newArrayList(request);
    }

    private Tag extractTag(String name, Node node) {
        String tagId = fromNode(name, node);
        return new Tag(tagId, "tag");
    }

    private Collection<String> fromCSV(String s) {
        return Arrays.asList(s.split(","));
    }

    private List<String> extractMultiField(Node node, String field) throws RepositoryException {
        List<String> values = new ArrayList<>();
        if (node.hasProperty(field)) {
             Property p = node.getProperty(field);
            if (p.isMultiple()) {
                for (Value author : p.getValues()) {
                    values.add(author.getString());
                }
            }
        }
        return  values;
    }

    private List<SlideRequest> processSlides(Node node) throws RepositoryException {

        List<SlideRequest> slides = new ArrayList<>();

        if (!node.hasNode(SlideshowsJCRSchema.slides.name()))
            return Collections.emptyList();


        NodeIterator iterator = node.getNode(SlideshowsJCRSchema.slides.name()).getNodes();
        while (iterator.hasNext()) {

            Node slide = iterator.nextNode();

            SlideRequest.SlideRequestBuilder slideBuilder = SlideRequest.builder()
                    .title(fromNode(SlideshowsJCRSchema.slideTitle.name(), slide))
                    .description(fromNode(SlideshowsJCRSchema.slideDescription.name(), slide))
                    .showAd(Boolean.valueOf(fromNode(SlideshowsJCRSchema.showAd.name(), slide)));

            Optional<AssetUploadResult> uploadResult = slideshowsAssetProcessor.uploadAssetFrom(slide);
            if (uploadResult.isPresent()) {
                slideBuilder.imageId(uploadResult.get().getId());
            }

            slides.add(slideBuilder.build());
        }

        return slides;
    }

    private String fromNode(String name, Node node) {

        try {
            Property property = node.getProperty(name);
            return property.getValue().getString();
        } catch (RepositoryException e) {
            log.error(e.getMessage());
            return "";
        }
    }

}


