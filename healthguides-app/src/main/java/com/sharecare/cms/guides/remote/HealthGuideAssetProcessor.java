package com.sharecare.cms.guides.remote;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.Optional;

/**
 * Created by robert.davis on 12/21/2016.
 */

public interface HealthGuideAssetProcessor {
   Optional<HealthGuideUploadResult> uploadAssetFrom(Node node) throws RepositoryException;
}

