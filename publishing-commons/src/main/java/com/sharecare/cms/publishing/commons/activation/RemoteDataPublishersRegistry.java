package com.sharecare.cms.publishing.commons.activation;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;

public interface RemoteDataPublishersRegistry {

	RemoteDataPublisher forNode(NodeType node);
}
