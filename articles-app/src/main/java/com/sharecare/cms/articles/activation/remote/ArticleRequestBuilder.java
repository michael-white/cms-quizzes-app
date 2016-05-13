package com.sharecare.cms.articles.activation.remote;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import java.util.List;

import com.sharecare.articles.sdk.model.Article;


public interface ArticleRequestBuilder {

	List<Article> forNode(Node node) throws RepositoryException;
}
