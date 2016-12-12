package com.sharecare.cms.publishing.commons.formatter;

import info.magnolia.ui.workbench.column.definition.AbstractColumnDefinition;

public class MultiSiteActivationStatusDefinition  extends AbstractColumnDefinition {

	public MultiSiteActivationStatusDefinition() {
		setFormatterClass(MultiSiteActivationStatusFormatter.class);
	}

}
