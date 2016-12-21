package com.sharecare.cms.publishing.commons.activation;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.util.Optional;

import info.magnolia.context.Context;
import info.magnolia.module.activation.commands.ActivationCommand;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublishingRemoteDataActivationCommand extends ActivationCommand {


	private String environment;

	private final Provider<RemoteDataPublishersRegistry> registry;

	@Inject
	public PublishingRemoteDataActivationCommand(Provider<RemoteDataPublishersRegistry> registry) {
		this.registry = registry;
	}

	@Override
	public boolean execute(Context ctx) throws Exception {

		final Node node = getJCRNode(ctx);

		Optional<RemoteDataPublisher> publisher = registry.get().forNode(node.getPrimaryNodeType());
		return !publisher.isPresent() || publisher.get().publish(node, environment) && super.execute(ctx);
	}


}
