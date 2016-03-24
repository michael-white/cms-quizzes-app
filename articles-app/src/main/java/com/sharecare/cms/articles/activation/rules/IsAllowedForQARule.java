package com.sharecare.cms.articles.activation.rules;

import javax.inject.Inject;

import com.sharecare.cms.articles.configuration.ArticlesModuleConfig;
import info.magnolia.ui.api.availability.AbstractAvailabilityRule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IsAllowedForQARule extends AbstractAvailabilityRule {

	private final ArticlesModuleConfig articlesModuleConfig;

	@Inject
	public IsAllowedForQARule(ArticlesModuleConfig articlesModuleConfig) {
		this.articlesModuleConfig = articlesModuleConfig;
	}

	@Override
	protected boolean isAvailableForItem(Object itemId) {
		return  articlesModuleConfig.getEnvironment().equals(ArticlesModuleConfig.Enivironments.qa.name());

	}
}