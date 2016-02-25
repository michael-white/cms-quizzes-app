package com.sharecare.cms.articles.activation.publishing;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.jcr.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.sharecare.articles.sdk.*;
import com.sharecare.cms.articles.activation.remote.ArticleAssetProcessor;
import com.sharecare.cms.articles.activation.remote.ArticleRequestBuilder;
import com.sharecare.cms.articles.activation.remote.ArticlesUploadResult;
import com.sharecare.cms.articles.configuration.ArticlesModuleConfig;
import com.sharecare.cms.articles.configuration.RemotePublishResourceConfig;
import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

@Slf4j
public class RemoteArticlePublisher implements RemoteDataPublisher {

	private static final String ARTICLES_NODE_TYPE = "mgnl:article";

	private final Map<String, ArticlesApiClient> clientMap;
	private final ArticleRequestBuilder articleRequestBuilder;
	private final ArticleAssetProcessor articleAssetProcessor;

	@Inject
	public RemoteArticlePublisher(ArticlesModuleConfig articlesModuleConfig,
								  ArticleRequestBuilder articleRequestBuilder,
								  ArticleAssetProcessor articleAssetProcessor) {
		this.articleAssetProcessor = articleAssetProcessor;
		this.clientMap = buildApiClients(articlesModuleConfig);
		this.articleRequestBuilder = articleRequestBuilder;
	}

	@Override
	public boolean publish(Node node, String environment) {

		try {
			log.info("Publishing {}:{} content to {} ", node.getPrimaryNodeType().getName(), node.getIdentifier(), environment);

			ArticlesApiClient client = clientMap.get(environment);
			List<Article> articleRequests = articleRequestBuilder.forNode(node);
			Optional<ArticlesUploadResult> uploadResult = articleAssetProcessor.uploadAssetFrom(node);
			if (uploadResult.isPresent()) {
				ArticlesUploadResult response = uploadResult.get();
				articleRequests.stream().forEach((a -> a.setImageUrl(response.getUrl())));
			}

			BasicResponse response = client.postRequest(articleRequests).toUrl("/articles").execute();

			if (response.getStatusCode() == 200) {
				log.info("Successfully published content item {}:{}", node.getPrimaryNodeType().getName(), node.getIdentifier());
				return markItemsAsActivated(node, environment);
			}

		} catch (RepositoryException e) {
			log.error("Failed Activation of article  {} ", ExceptionUtils.getFullStackTrace(e));
		}

		return false;
	}

	@Override
	public boolean unPublish(Node node, String environment) {

		try {

			log.warn("Deleting {}:{} content from {} ", node.getPrimaryNodeType().getName(), node.getIdentifier(), environment);
			ArticlesApiClient client = clientMap.get(environment);
			BasicResponse response = client.deleteRequest().execute();

			if (response.getStatusCode() == 200) {
				log.info("Successfully published content item {}:{}", node.getPrimaryNodeType().getName(), node.getIdentifier());
				return markItemsAsDeActivated(node, environment);
			}

		} catch (RepositoryException e) {
			log.error("Failed De-Activation of article  {} ", ExceptionUtils.getFullStackTrace(e));
		}

		return false;
	}

	private boolean markItemsAsDeActivated(Node node, String environment) {
		// TODO Implement this
			return true;
	}

	private boolean markItemsAsActivated(Node item, String environment) throws RepositoryException {

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
			return true;
		} catch (RepositoryException e) {
			log.error("Failed to mark as activated {} ", ExceptionUtils.getFullStackTrace(e));
			unPublish(item, environment);
			return false;
		}
	}

	private Map<String, ArticlesApiClient> buildApiClients(ArticlesModuleConfig articlesModuleConfig) {
		return Maps.transformValues(articlesModuleConfig.getPublishing(), new Function<RemotePublishResourceConfig, ArticlesApiClient>() {
			@Nullable
			@Override
			public ArticlesApiClient apply(@Nullable RemotePublishResourceConfig config) {
				assert config != null;
				BasicAuthCredentials basicAuthCredentials = new BasicAuthCredentials(config.getUsername(), config.getPassword());
				ServerInfo serverInfo = new ServerInfo(config.getHostProtocol(), config.getHostAddress(), config.getHostPort());
				return new ArticlesApiClient(basicAuthCredentials, serverInfo);

			}
		});
	}

	@Override
	public boolean canService(String nodeType) {
		return ARTICLES_NODE_TYPE.equals(nodeType);
	}
}
