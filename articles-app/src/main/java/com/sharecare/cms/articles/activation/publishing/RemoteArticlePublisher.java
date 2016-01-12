package com.sharecare.cms.articles.activation.publishing;

import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;

public class RemoteArticlePublisher implements RemoteDataPublisher {

	@Override
	public boolean publishTo(String environment) {
		return false;
	}
}
