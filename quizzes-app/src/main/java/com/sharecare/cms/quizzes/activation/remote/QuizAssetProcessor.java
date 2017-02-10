package com.sharecare.cms.quizzes.activation.remote;

import com.sharecare.cms.cloudinary.dam.AssetUploadResult;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Optional;

public interface QuizAssetProcessor {
    Optional<AssetUploadResult> uploadAssetFrom(Node node, String propertyName) throws RepositoryException;
}

