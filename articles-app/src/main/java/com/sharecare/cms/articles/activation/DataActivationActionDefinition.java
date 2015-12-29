package com.sharecare.cms.articles.activation;

import info.magnolia.ui.framework.action.ActivationActionDefinition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DataActivationActionDefinition extends ActivationActionDefinition {

	private String environment;

	public DataActivationActionDefinition() {
		setImplementationClass(DataActivationAction.class);
	}



}