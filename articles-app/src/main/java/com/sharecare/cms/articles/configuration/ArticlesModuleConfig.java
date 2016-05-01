package com.sharecare.cms.articles.configuration;

import java.util.Map;

import lombok.Data;

@Data
public class ArticlesModuleConfig {

	public enum Enivironments {
		production ,qa, localhost
	}


	private Map<String, Map<String,RemoteServerResourceConfig>> publishing;
	private Map<String,String> webHost;
	private RemoteServerResourceConfig tagResource;
	private String environment;

}
