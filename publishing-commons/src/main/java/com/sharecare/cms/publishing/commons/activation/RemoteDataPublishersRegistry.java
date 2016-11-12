package com.sharecare.cms.publishing.commons.activation;

import javax.jcr.nodetype.NodeType;
import java.util.Optional;

public interface RemoteDataPublishersRegistry {

	Optional<RemoteDataPublisher> forNode(NodeType node);

}
