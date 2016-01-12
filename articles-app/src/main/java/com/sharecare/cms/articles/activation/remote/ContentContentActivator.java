package com.sharecare.cms.articles.activation.remote;

import java.util.List;

public interface ContentContentActivator {


	interface ActivationResult {
		boolean isSuccess();

		String getErrorMessage();
	}

	ActivationResult activate(List<ArticleRequestFactory.ArticleRequest> requests);

	ActivationResult activate(ArticleRequestFactory.ArticleRequest request);

}
