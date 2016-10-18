package com.sharecare.cms.slideshows.activation.remote;

import com.sharecare.cms.cloudinary.dam.AssetUploadResult;
import com.sharecare.cms.slideshows.activation.remote.sdk.SlideshowsRequest;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.List;
import java.util.Optional;


public interface SlideshowsRequestBuilder {

	List<SlideshowsRequest> forNode(Node node, Optional<AssetUploadResult> uploadResult) throws RepositoryException;
}
