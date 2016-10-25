package com.sharecare.cms.slideshows.activation.remote;

import com.sharecare.cms.cloudinary.dam.AssetUploadResult;
import com.sharecare.slideshows.sdk.model.SlideshowRequest;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.List;


public interface SlideshowsRequestBuilder {

	List<SlideshowRequest> forNode(Node node, List<AssetUploadResult> uploadResult) throws RepositoryException;
}
