package com.sharecare.cms.guides.activation.remote;

import com.sharecare.healthguides.sdk.model.HealthGuideRequest;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

public interface HealthGuideRequestBuilder {
    HealthGuideRequest forNode(Node node) throws RepositoryException;
}
