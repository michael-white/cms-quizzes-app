package com.sharecare.cms.publishing.commons;

import com.sharecare.cms.publishing.commons.activation.RemoteServiceResponseProcessor;
import com.sharecare.core.sdk.BasicResponse;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;

public interface ServiceResponseProcessor {
    boolean processResponse(Node node, String environment, BasicResponse response, RemoteServiceResponseProcessor.StatusUpdater<ValueFactory, Node, String> addEnvironmentCallback) throws RepositoryException;
}
