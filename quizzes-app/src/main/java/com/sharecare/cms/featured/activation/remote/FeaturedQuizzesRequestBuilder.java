package com.sharecare.cms.featured.activation.remote;

import com.sharecare.quizzes.sdk.featured.model.FeaturedQuizRequest;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public interface FeaturedQuizzesRequestBuilder {

    FeaturedQuizRequest forNode(Node node) throws RepositoryException;
}
