package com.sharecare.cms.articles.configuration;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class RemotePublishResourceConfig {

	private String hostProtocol;
	private String hostAddress;
	private Integer hostPort;
	private String username;
	private String password;
}
