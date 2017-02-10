package com.sharecare.cms.quizzes.activation.remote;

import com.sharecare.quizzes.sdk.model.QuizRequest;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public interface QuizzesRequestBuilder {
    QuizRequest forNode(Node node, String environment) throws RepositoryException;
}
