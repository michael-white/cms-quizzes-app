package com.sharecare.cms.articles.activation.publishing;

import com.sharecare.cms.articles.activation.remote.ArticleAssetProcessor;
import com.sharecare.cms.articles.activation.remote.ArticleRequestBuilder;
import com.sharecare.cms.articles.activation.remote.CloudinaryArticlesAssetProcessor;
import com.sharecare.cms.articles.activation.remote.RemoteArticleRequestBuilder;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.RemoteTagService;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagService;
import com.sharecare.cms.publishing.commons.configuration.ComponentBindingConfigurer;

public class ArticlesPublishingBindingConfigurer extends ComponentBindingConfigurer {

	@Override
	protected void configureActions() {
		bindPublisher().to(RemoteArticlePublisher.class);
		bindPublisher().to(RemoteArticleFolderPublisher.class);

		binder().bind(ArticleRequestBuilder.class).to(RemoteArticleRequestBuilder.class);
		binder().bind(ArticleAssetProcessor.class).to(CloudinaryArticlesAssetProcessor.class);
		binder().bind(TagService.class).to(RemoteTagService.class);
	}
}
