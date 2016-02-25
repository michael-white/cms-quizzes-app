package com.sharecare.cms.articles.activation.remote;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Optional;

public interface ArticleAssetProcessor {

	Optional<ArticlesUploadResult> uploadAssetFrom(Node node) throws RepositoryException;
}
