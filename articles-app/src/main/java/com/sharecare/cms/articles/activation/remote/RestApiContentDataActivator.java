package com.sharecare.cms.articles.activation.remote;

import java.util.List;

import com.sharecare.cms.articles.activation.DataActivationAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RestApiContentDataActivator implements ContentContentActivator {

	private static final Logger log = LoggerFactory.getLogger(DataActivationAction.class);

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
}
