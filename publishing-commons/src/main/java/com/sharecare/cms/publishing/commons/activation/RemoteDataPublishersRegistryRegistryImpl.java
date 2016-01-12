package com.sharecare.cms.publishing.commons.activation;

import javax.inject.Inject;
import javax.jcr.Node;
import java.util.List;
import java.util.Set;

public class RemoteDataPublishersRegistryRegistryImpl implements RemoteDataPublishersRegistry {

	private final Set<RemoteDataPublisher> publishers;

	@Inject
	public RemoteDataPublishersRegistryRegistryImpl(Set<RemoteDataPublisher> publishers) {
		this.publishers = publishers;
	}

	@Override
	public RemoteDataPublisher forNode(Node node) {
		return null;
	}
}
