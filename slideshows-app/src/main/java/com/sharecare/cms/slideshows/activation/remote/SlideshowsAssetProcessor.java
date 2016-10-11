package com.sharecare.cms.slideshows.activation.remote;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Optional;

public interface SlideshowsAssetProcessor {

	Optional<ArticlesUploadResult> uploadAssetFrom(Node node) throws RepositoryException;
}
