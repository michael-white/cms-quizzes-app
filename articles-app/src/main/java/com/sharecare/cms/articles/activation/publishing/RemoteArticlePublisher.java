package com.sharecare.cms.articles.activation.publishing;

import javax.inject.Inject;
import javax.jcr.*;
import java.util.Arrays;

import com.sharecare.cms.articles.activation.remote.ArticleRequestFactory;
import com.sharecare.cms.articles.activation.remote.ContentContentActivator;
import com.sharecare.cms.articles.activation.remote.RestApiContentDataActivator;
import com.sharecare.cms.articles.configuration.ArticlesModuleConfig;
import com.sharecare.cms.articles.configuration.RemotePublishResourceConfig;
import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteArticlePublisher implements RemoteDataPublisher {

	private static final String ARTICLES_NODE_TYPE = "mgnl:article";

	private final ArticlesModuleConfig articlesModuleConfig;

	@Inject
	public RemoteArticlePublisher(ArticlesModuleConfig articlesModuleConfig) {
		this.articlesModuleConfig = articlesModuleConfig;
	}

	@Override
	public boolean publish(Node node, String environment) {

		try {
			log.warn("Publishing {}:{} content to {} ", node.getPrimaryNodeType().getName(), node.getIdentifier(), environment);

			RemotePublishResourceConfig config = articlesModuleConfig.forEnvironment(environment);
			if (true) return true;
			ArticleRequestFactory.ArticleRequest request = buildArticleActivationRequests();

			ContentContentActivator activator = new RestApiContentDataActivator();
			ContentContentActivator.ActivationResult result = activator.activate(request);

			if (result.isSuccess()) {
				try {
					markItemsAsActivated(node, environment);
				} catch (RepositoryException e) {
					log.error("Failed Activation of article  {} ", e.getMessage());
					return false;
				}
				return true;
			}

			return false;
		} catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean unPublish(Node node, String environment) {
		RemotePublishResourceConfig config = articlesModuleConfig.forEnvironment(environment);

		try {
			log.warn("UN Publishing {}:{} content from {} ", node.getPrimaryNodeType().getName(), node.getIdentifier(), environment);
		} catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		}

		return true;
//		throw new UnsupportedOperationException("NOT IMPLEMENTED YET");
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
