package com.sharecare.cms.articles.activation.remote;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class RestApiContentDataActivator implements ContentContentActivator {


	@Override
	public ActivationResult activate(List<ArticleRequestFactory.ArticleRequest> requests) {
		log.debug("Processing {} requests out for activation", requests.size());

		return new ActivationResult() {
			@Override
			public boolean isSuccess() {
				return true;
			}

			@Override
			public String getErrorMessage() {
				return "";
			}
		};
	}

	@Override
	public ActivationResult activate(ArticleRequestFactory.ArticleRequest request) {
		return null;
	}
}
