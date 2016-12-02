package com.sharecare.cms.featured.remote;

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

    @Override
    public FeaturedSlideshowRequest forNode(Node node) throws RepositoryException {

        // Before we send off the request to the API service, we have some
        // housecleaning to do.  Because the data coming from the selector
        // has the full JCR path including folder name, we need to strip that off.
        String hero = fromNode(FeaturedJCRSchema.hero.name(), node);
        hero = stripPath(hero);

        List<String> uriList = extractMultiField(node, FeaturedJCRSchema.carousel.name());
        List<String> carousel = uriList.stream()
                .map(this::stripPath)
                .collect(Collectors.toList());

        FeaturedSlideshowRequest request = FeaturedSlideshowRequest.builder()
                .hero(hero)
                .carousel(carousel)
                .build();

        log.info("Created Featured request object {} ", request);

        return request;
    }

    private String stripPath(String hero) {
        return hero.substring(hero.lastIndexOf("/") + 1, hero.length());
    }

}
