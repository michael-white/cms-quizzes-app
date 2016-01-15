package com.sharecare.cms.articles.configuration;

import java.util.Map;

import lombok.Data;

@Data
public class ArticlesModuleConfig {

	private Map<String,RemotePublishResourceConfig> publishing;

	public RemotePublishResourceConfig forEnvironment(String environment) {
		return publishing.get(environment);
	}
}