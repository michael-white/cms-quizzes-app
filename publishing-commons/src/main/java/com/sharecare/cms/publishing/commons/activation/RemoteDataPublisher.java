package com.sharecare.cms.publishing.commons.activation;

import javax.jcr.Node;

public interface RemoteDataPublisher {

	boolean publish(Node node, String environment);

	boolean unPublish(Node node, String environment);

	boolean canService(String nodeType);

}
