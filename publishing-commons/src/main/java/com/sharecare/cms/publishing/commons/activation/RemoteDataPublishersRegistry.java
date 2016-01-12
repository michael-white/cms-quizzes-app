package com.sharecare.cms.publishing.commons.activation;

import javax.jcr.Node;

public interface RemoteDataPublishersRegistry {

	RemoteDataPublisher forNode(Node node);
}
