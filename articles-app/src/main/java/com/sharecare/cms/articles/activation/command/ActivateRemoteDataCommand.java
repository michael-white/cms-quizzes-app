package com.sharecare.cms.articles.activation.command;

import javax.jcr.Node;

import info.magnolia.context.Context;
import info.magnolia.module.activation.commands.ActivationCommand;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateRemoteDataCommand extends ActivationCommand {

	private String environment;

	@Override
	public boolean execute(Context ctx) throws Exception {

		final Node node = getJCRNode(ctx);
		if (node.isNodeType("mgnl:article")) {
			publishRemote(node);
		}
		return super.execute(ctx);
	}

	private void publishRemote(Node node) {

	}
}
