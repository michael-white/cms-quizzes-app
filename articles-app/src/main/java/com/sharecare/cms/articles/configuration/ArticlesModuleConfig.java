package com.sharecare.cms.articles.configuration;

import java.util.Map;

import lombok.Data;

@Data
public class ArticlesModuleConfig {

	public enum Enivironments {
		prod,qa
	}


	private Map<String,RemoteServerResourceConfig> publishing;
	private RemoteServerResourceConfig tagResource;
	private String environment;

}
