package com.sharecare.cms.featured.remote;

import com.sharecare.sdk.slideshows.featured.model.FeaturedSlideshowRequest;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public interface FeaturedSlideshowsRequestBuilder {

	FeaturedSlideshowRequest forNode(Node node) throws RepositoryException;
}
