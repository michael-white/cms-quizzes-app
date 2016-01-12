package com.sharecare.cms.articles.activation.publishing;

import javax.jcr.*;

import java.util.Arrays;

import com.sharecare.cms.articles.activation.remote.ArticleRequestFactory;
import com.sharecare.cms.articles.activation.remote.ContentContentActivator;
import com.sharecare.cms.articles.activation.remote.RestApiContentDataActivator;
import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteArticlePublisher implements RemoteDataPublisher {

	private static final String ARTICLES_NODE_TYPE = "mgnl:article";


	@Override
	public boolean publish(Node node, String environment) {

		if (true) return true;

		ArticleRequestFactory.ArticleRequest request = buildArticleActivationRequests();

		ContentContentActivator activator = new RestApiContentDataActivator();
		ContentContentActivator.ActivationResult result = activator.activate(request);

		if (result.isSuccess()) {
			try {
				markItemsAsActivated(node, environment);
			} catch (RepositoryException e) {
				log.error("Failed Activation of article  {} ",e.getMessage());
				return false;
			}
			return true;
		}

		return false;
	}

	private ArticleRequestFactory.ArticleRequest buildArticleActivationRequests() {
		return null;
	}

	private void markItemsAsActivated(Node item, String environment) throws RepositoryException {

		log.debug("Marking item {} as active on environment {}", item.getIdentifier(), environment);
		try {
			Session session = item.getSession();
			ValueFactory valueFactory = session.getValueFactory();

			if (item.hasProperty("active-status")) {
				Property p = item.getProperty("active-status");
				Value[] values = Arrays.copyOf(p.getValues(), p.getValues().length + 1);
				values[p.getValues().length] = valueFactory.createValue(environment);
				p.setValue(values);
			} else {
				Value[] values = new Value[]{valueFactory.createValue(environment)};
				item.setProperty("active-status", values);
			}

			session.save();
		} catch (RepositoryException e) {
			// TODO revert the sync
			log.error("Failed Activation of article  {} ", item.getIdentifier());
		}
	}

	@Override
	public boolean canService(String nodeType) {
		return ARTICLES_NODE_TYPE.equals(nodeType);
	}
}
