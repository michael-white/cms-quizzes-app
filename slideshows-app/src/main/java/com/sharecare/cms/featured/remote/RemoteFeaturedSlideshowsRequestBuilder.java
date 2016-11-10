package com.sharecare.cms.featured.remote;

import com.google.inject.Inject;
import com.sharecare.cms.featured.schema.FeaturedJCRSchema;
import com.sharecare.sdk.slideshows.featured.model.FeaturedSlideshowRequest;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.List;
import java.util.stream.Collectors;

import static com.sharecare.cms.utils.NodeUtils.extractMultiField;
import static com.sharecare.cms.utils.NodeUtils.fromNode;

@Slf4j
public class RemoteFeaturedSlideshowsRequestBuilder implements FeaturedSlideshowsRequestBuilder {

    @Inject
    public RemoteFeaturedSlideshowsRequestBuilder() {
    }

    @Override
    public FeaturedSlideshowRequest forNode(Node node) throws RepositoryException {

        // Before we send off the request to the API service, we have some
        // housecleaning to do.  Because the data coming from the selector
        // starts with a `/` character, we need to strip that off.
        String hero = fromNode(FeaturedJCRSchema.hero.name(), node);
        if (hero.startsWith("/"))
            hero = hero.substring(1);

        List<String> uriList = extractMultiField(node, FeaturedJCRSchema.carousel.name());
        List<String> carousel = uriList.stream()
                .map(slideshow -> slideshow.startsWith("/")
                        ? slideshow.substring(1)
                        : slideshow).collect(Collectors.toList());

        FeaturedSlideshowRequest request = FeaturedSlideshowRequest.builder()
                .hero(hero)
                .carousel(carousel)
                .build();

        log.info("Created Featured request object {} ", request);

        return request;
    }

}
