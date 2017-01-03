package com.sharecare.cms.guides.activation.remote;

import com.sharecare.cms.cloudinary.dam.AssetUploadResult;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Optional;

public interface HealthGuideAssetProcessor {
   Optional<AssetUploadResult> uploadAssetFrom(Node node) throws RepositoryException;
}

