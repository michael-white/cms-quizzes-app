package com.sharecare.cms.publishing.commons.activation;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NodeType;
import java.util.Optional;

public interface RemoteDataPublishersRegistry {

	Optional<RemoteDataPublisher> forNode(NodeType node);

}
