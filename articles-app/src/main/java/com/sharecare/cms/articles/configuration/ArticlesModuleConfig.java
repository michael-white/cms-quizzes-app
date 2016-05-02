package com.sharecare.cms.articles.configuration;

import java.util.Map;

import lombok.Data;

@Data
public class ArticlesModuleConfig {

	public enum Environments {
		release,qa, dev
	}


	private Map<String, Map<String,RemoteServerResourceConfig>> publishing;
	private Map<String,String> webHost;
	private RemoteServerResourceConfig tagResource;
	private String environment;

}
