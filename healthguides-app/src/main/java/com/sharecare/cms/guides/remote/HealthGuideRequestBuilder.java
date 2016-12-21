package com.sharecare.cms.guides.remote;

import com.sharecare.healthguides.sdk.model.HealthGuideRequest;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.List;
import java.util.Optional;

/**
 * Created by robert.davis on 12/21/2016.
 */
public interface HealthGuideRequestBuilder {
    List<HealthGuideRequest> forNode(Node node, Optional<HealthGuideUploadResult> uploadResult) throws RepositoryException;
}
