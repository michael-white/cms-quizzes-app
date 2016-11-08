package com.sharecare.cms.featured.remote;

import com.sharecare.cms.cloudinary.dam.AssetUploadResult;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Optional;

public interface FeaturedSlideshowsAssetProcessor {

	Optional<AssetUploadResult> uploadAssetFrom(Node node) throws RepositoryException;
}
