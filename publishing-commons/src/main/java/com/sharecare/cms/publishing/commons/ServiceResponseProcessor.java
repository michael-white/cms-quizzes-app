package com.sharecare.cms.publishing.commons;

import com.sharecare.core.sdk.BasicResponse;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public interface ServiceResponseProcessor {
    boolean processResponse(Node node, String environment, BasicResponse response) throws RepositoryException;
}
