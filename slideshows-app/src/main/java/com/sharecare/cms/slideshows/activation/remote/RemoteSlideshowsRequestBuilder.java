package com.sharecare.cms.slideshows.activation.remote;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sharecare.cms.cloudinary.dam.AssetUploadResult;
import com.sharecare.cms.slideshows.schema.SlideshowsJCRSchema;
import com.sharecare.slideshows.sdk.model.SlideRequest;
import com.sharecare.slideshows.sdk.model.SlideshowRequest;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class RemoteSlideshowsRequestBuilder implements SlideshowsRequestBuilder {

    private final SlideshowsAssetProcessor slideshowsAssetProcessor;

    @Inject
    public RemoteSlideshowsRequestBuilder(SlideshowsAssetProcessor slideshowsAssetProcessor) {
        this.slideshowsAssetProcessor = slideshowsAssetProcessor;
    }

    @Override
    public List<SlideshowRequest> forNode(Node node) throws RepositoryException {

        SlideshowRequest request = SlideshowRequest.builder()
                .id(node.getIdentifier())
                .uri(node.getName())
                .title(fromNode(SlideshowsJCRSchema.title.name(), node))
                .description(fromNode(SlideshowsJCRSchema.description.name(), node))
                .slides(processSlides(node))
                .build();


        return Lists.newArrayList(request);
    }

    private List<SlideRequest> processSlides(Node node) throws RepositoryException {

        List<SlideRequest> slides = new ArrayList<>();

        if (!node.hasNode(SlideshowsJCRSchema.slides.name()))
            return Collections.emptyList();


        NodeIterator iterator = node.getNode(SlideshowsJCRSchema.slides.name()).getNodes();
        while (iterator.hasNext()) {

            Node slide = iterator.nextNode();

            SlideRequest.SlideRequestBuilder slideBuilder = SlideRequest.builder()
                    .title(fromNode(SlideshowsJCRSchema.title.name(), slide))
                    .description(fromNode(SlideshowsJCRSchema.description.name(), slide))
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


