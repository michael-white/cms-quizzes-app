package com.sharecare.cms.publishing.commons.activation;

import javax.inject.Inject;
import javax.jcr.nodetype.NodeType;
import java.util.Set;

public class RemoteDataPublishersRegistryRegistryImpl implements RemoteDataPublishersRegistry {

	private final Set<RemoteDataPublisher> publishers;

	@Inject
	public RemoteDataPublishersRegistryRegistryImpl(Set<RemoteDataPublisher> publishers) {
		this.publishers = publishers;
	}

	@Override
	public RemoteDataPublisher forNode(NodeType nodeType) {

		for (RemoteDataPublisher publisher : publishers) {
			if(publisher.canService(nodeType.getName()))
				return publisher;
		}

		throw new UnsupportedOperationException("Could not find a handler for type");
	}
}
