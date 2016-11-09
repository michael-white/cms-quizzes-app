package com.sharecare.cms.featured.remote;

import com.google.inject.Inject;
import com.sharecare.cms.featured.schema.FeaturedJCRSchema;
import com.sharecare.sdk.slideshows.featured.model.FeaturedSlideshowRequest;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import static com.sharecare.cms.utils.NodeUtils.extractMultiField;
import static com.sharecare.cms.utils.NodeUtils.fromNode;

public class RemoteFeaturedSlideshowsRequestBuilder implements FeaturedSlideshowsRequestBuilder {

    @Inject
    public RemoteFeaturedSlideshowsRequestBuilder() {
    }

    @Override
    public FeaturedSlideshowRequest forNode(Node node) throws RepositoryException {

        FeaturedSlideshowRequest request = FeaturedSlideshowRequest.builder()
                .hero(fromNode(FeaturedJCRSchema.hero.name(), node))
                .carousel(extractMultiField(node, FeaturedJCRSchema.carousel.name()))
                .build();

        return request;
    }

}
