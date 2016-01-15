package com.sharecare.cms.publishing.commons.activation;

import javax.inject.Inject;
import javax.jcr.Node;
import java.util.Optional;

import info.magnolia.context.Context;
import info.magnolia.module.activation.commands.ActivationCommand;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PublishingRemoteDataActivationCommand extends ActivationCommand {


	private String environment;

	private final RemoteDataPublishersRegistry registry;

	@Inject
	public PublishingRemoteDataActivationCommand(RemoteDataPublishersRegistry registry) {
		this.registry = registry;
	}

	@Override
	public boolean execute(Context ctx) throws Exception {

		final Node node = getJCRNode(ctx);

		Optional<RemoteDataPublisher> publisher = registry.forNode(node.getPrimaryNodeType());
		return !publisher.isPresent() || publisher.get().publish(node, environment) && super.execute(ctx);
	}


}
