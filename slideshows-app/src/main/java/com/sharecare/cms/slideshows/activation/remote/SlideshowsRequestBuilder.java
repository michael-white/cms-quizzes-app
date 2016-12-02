package com.sharecare.cms.slideshows.activation.remote;

import com.sharecare.sdk.slideshows.model.SlideshowRequest;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public interface SlideshowsRequestBuilder {

	SlideshowRequest forNode(Node node) throws RepositoryException;
}
