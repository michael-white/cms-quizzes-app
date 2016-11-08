package com.sharecare.cms.featured.remote;

import com.google.inject.Inject;
import com.sharecare.cms.featured.schema.FeaturedJCRSchema;
import com.sharecare.sdk.slideshows.featured.model.FeaturedSlideshowRequest;
import com.sharecare.sdk.slideshows.featured.model.HeroSlideshowRequest;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import static com.sharecare.cms.utils.NodeUtils.extractMultiField;

public class RemoteFeaturedSlideshowsRequestBuilder implements FeaturedSlideshowsRequestBuilder {

    private final FeaturedSlideshowsAssetProcessor featuredSlideshowsAssetProcessor;

    @Inject
    public RemoteFeaturedSlideshowsRequestBuilder(FeaturedSlideshowsAssetProcessor featuredSlideshowsAssetProcessor) {
        this.featuredSlideshowsAssetProcessor = featuredSlideshowsAssetProcessor;
    }

    @Override
    public FeaturedSlideshowRequest forNode(Node node) throws RepositoryException {

        HeroSlideshowRequest heroSlideshowRequest = HeroSlideshowRequest.builder().
                // TODO : Need image handling/Cloudinary code here
                build();

        FeaturedSlideshowRequest request = FeaturedSlideshowRequest.builder()
                .hero(heroSlideshowRequest)
                .carousel(extractMultiField(node, FeaturedJCRSchema.carousel.name()))
                .build();

        return request;
    }

}


