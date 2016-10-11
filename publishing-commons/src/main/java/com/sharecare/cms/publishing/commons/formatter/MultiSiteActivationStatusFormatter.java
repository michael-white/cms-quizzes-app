package com.sharecare.cms.publishing.commons.formatter;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;

import com.sharecare.cms.publishing.commons.ui.taglib.activation.EnvironmentActivationField;
import com.vaadin.ui.Table;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.ui.workbench.column.AbstractColumnFormatter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultiSiteActivationStatusFormatter extends AbstractColumnFormatter<MultiSiteActivationStatusDefinition> {

	public MultiSiteActivationStatusFormatter(MultiSiteActivationStatusDefinition definition) {
		super(definition);
	}

	@Override
	public Object generateCell(Table source, Object itemId, Object columnId) {
		final Item jcrItem = getJcrItem(source, itemId);

		if (jcrItem != null && jcrItem.isNode()) {
			Node node = (Node) jcrItem;
			try {

				if (NodeUtil.isNodeType(node, NodeTypes.Content.NAME)) {
					String icon = "<span class=\"icon-shape-circle activation-status  %s \"></span>";

					if ( node.hasProperty(EnvironmentActivationField.ACTIVE_STATUS_FIELD)) {
						Property p = node.getProperty(EnvironmentActivationField.ACTIVE_STATUS_FIELD);
						if (p.getValues() != null && p.getValues().length > 0) {
							return String.format(icon, "color-green");
						}
					} else {
						return String.format(icon, "color-red");
					}
				}
			} catch (RepositoryException e) {
				log.warn("Unable to get name of user for column", e);
			}
		}
		return "";
	}
}
