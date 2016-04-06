package com.sharecare.cms.articles.activation.publishing;

import info.magnolia.ui.framework.action.ActivationActionDefinition;

public class ArticleActivationActionDefinition  extends ActivationActionDefinition {

	public ArticleActivationActionDefinition() {
		setImplementationClass(ArticleActivationAction.class);
	}

}
