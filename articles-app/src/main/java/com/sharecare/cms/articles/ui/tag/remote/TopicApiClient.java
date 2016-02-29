package com.sharecare.cms.articles.ui.tag.remote;

import com.sharecare.cms.articles.configuration.RemoteServerResourceConfig;

public class TopicApiClient {

	private final RemoteServerResourceConfig tagResource;

	public TopicApiClient(RemoteServerResourceConfig tagResource) {
		this.tagResource = tagResource;
	}

	public BasicResponse<TopicResult> loadForTag(String tagId) {
		return null;
	}
}
