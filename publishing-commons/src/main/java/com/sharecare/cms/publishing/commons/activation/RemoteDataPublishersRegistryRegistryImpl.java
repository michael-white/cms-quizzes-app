package com.sharecare.cms.publishing.commons.activation;

import javax.inject.Inject;
import javax.jcr.nodetype.NodeType;
import java.util.Optional;
import java.util.Set;

public class RemoteDataPublishersRegistryRegistryImpl implements RemoteDataPublishersRegistry {

	private final Set<RemoteDataPublisher> publishers;

	@Inject
	public RemoteDataPublishersRegistryRegistryImpl(Set<RemoteDataPublisher> publishers) {
		this.publishers = publishers;
	}

	@Override
	public Optional<RemoteDataPublisher> forNode(NodeType nodeType) {

		for (RemoteDataPublisher publisher : publishers) {
			if(publisher.canService(nodeType.getName()))
				return Optional.of(publisher);
		}

		return Optional.empty();
	}

}
