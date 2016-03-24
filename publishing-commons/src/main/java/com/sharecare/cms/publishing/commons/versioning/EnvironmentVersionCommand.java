package com.sharecare.cms.publishing.commons.versioning;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.magnolia.cms.core.version.VersionManager;
import info.magnolia.cms.util.ExclusiveWrite;
import info.magnolia.commands.impl.VersionCommand;
import info.magnolia.context.Context;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.PropertyUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

@Getter
@Setter
public class EnvironmentVersionCommand extends VersionCommand {

	private final VersionManager versionManager;
	private String environment;


	@Inject
	public EnvironmentVersionCommand(final VersionManager versionManager) {
		super(versionManager);
		this.versionManager = versionManager;
	}


	@Override
	public boolean execute(Context ctx) throws Exception {
		final Node node = getJCRNode(ctx);
		if (isRecursive()) {
			// set versionMap and version name for this node
			List<Map<String, String>> versionMap = new ArrayList<Map<String, String>>();
			versionRecursively(node, ctx, versionMap);
			ctx.setAttribute(Context.ATTRIBUTE_VERSION_MAP, versionMap, Context.LOCAL_SCOPE);
		} else {
			addEnvironmentComment(node);
			Version version = versionManager.addVersion(node, getRule());
			if (version != null) {
				ctx.setAttribute(Context.ATTRIBUTE_VERSION, version.getName(), Context.LOCAL_SCOPE);
			}
		}

		return true;
	}

	private void versionRecursively(Node node, Context ctx, List<Map<String, String>> versionMap) throws RepositoryException {
		addEnvironmentComment(node);
		Version version = versionManager.addVersion(node, getRule());

		Map<String, String> entry = new HashMap<String, String>();
		entry.put("uuid", node.getIdentifier());
		if (version != null) {
			entry.put("version", version.getName());
			if (StringUtils.isEmpty(ctx.getAttribute(Context.ATTRIBUTE_VERSION))) {
				ctx.setAttribute(Context.ATTRIBUTE_VERSION, version.getName(), Context.LOCAL_SCOPE);
			}
		}
		versionMap.add(entry);

		NodeIterator children = node.getNodes();
		while (children.hasNext()) {
			Node child = children.nextNode();
			if (!getRule().isAllowed(child)) {
				versionRecursively(child, ctx, versionMap);
			}
		}
	}

	protected void addEnvironmentComment(final Node node) throws RepositoryException {
		synchronized (ExclusiveWrite.getInstance()) {
			PropertyUtil.setProperty(node, NodeTypes.Versionable.COMMENT, getComment() != null ? environment.toUpperCase() + " " + getComment() : environment.toUpperCase());
			node.getSession().save();
		}
	}
}
