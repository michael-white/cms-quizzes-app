package com.sharecare.cms.articles.activation.publishing;

import com.sharecare.cms.articles.activation.remote.ArticleRequestBuilder;
import com.sharecare.cms.articles.activation.remote.RemoteArticleRequestBuilder;
import com.sharecare.cms.publishing.commons.configuration.ComponentBindingConfigurer;

public class ArticlesPublishingBindingConfigurer extends ComponentBindingConfigurer {

	@Override
	protected void configureActions() {
		bindAction().to(RemoteArticlePublisher.class);

		binder().bind(ArticleRequestBuilder.class).to(RemoteArticleRequestBuilder.class);
	}
}
