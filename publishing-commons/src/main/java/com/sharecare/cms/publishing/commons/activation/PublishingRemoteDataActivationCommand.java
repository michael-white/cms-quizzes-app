package com.sharecare.cms.publishing.commons.activation;

import javax.inject.Inject;
import javax.jcr.Node;

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

		RemoteDataPublisher publisher = registry.forNode(node);

		return publisher.publishTo(environment) && super.execute(ctx);

		//		if (node.isNodeType("mgnl:article")) {
//			publishRemote(node);
//		}
//		return super.execute(ctx);
	}


}
