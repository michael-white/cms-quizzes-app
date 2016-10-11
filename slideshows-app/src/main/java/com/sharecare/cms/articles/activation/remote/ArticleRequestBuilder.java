package com.sharecare.cms.articles.activation.remote;

import com.sharecare.articles.sdk.model.ArticleRequest;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.List;
import java.util.Optional;


public interface ArticleRequestBuilder {

	List<ArticleRequest> forNode(Node node, Optional<ArticlesUploadResult> uploadResult) throws RepositoryException;
}
