package com.sharecare.cms.slideshows.activation.remote;

import com.sharecare.cms.cloudinary.dam.AssetUploadResult;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Optional;

public interface SlideshowsAssetProcessor {

	Optional<AssetUploadResult> uploadAssetFrom(Node node) throws RepositoryException;
}
