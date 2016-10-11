package com.sharecare.cms.articles.activation.rules;

import javax.inject.Inject;

import com.sharecare.cms.articles.configuration.ArticlesModuleConfig;
import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import info.magnolia.ui.api.availability.AbstractAvailabilityRule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IsAllowedForDevRule extends AbstractAvailabilityRule {

	private final CommonsModuleConfig commonsModuleConfig;

	@Inject
	public IsAllowedForDevRule(CommonsModuleConfig commonsModuleConfig) {
		this.commonsModuleConfig = commonsModuleConfig;
	}

	@Override
	protected boolean isAvailableForItem(Object itemId) {
		return  commonsModuleConfig.getEnvironment().equals(CommonsModuleConfig.Environments.dev.name());

	}
}
