package com.sharecare.cms.articles.activation.publishing;

import javax.inject.Inject;
import javax.jcr.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.sharecare.articles.sdk.*;
import com.sharecare.cms.articles.activation.remote.ArticleAssetProcessor;
import com.sharecare.cms.articles.activation.remote.ArticleRequestBuilder;
import com.sharecare.cms.articles.activation.remote.ArticlesUploadResult;
import com.sharecare.cms.articles.configuration.ArticlesModuleConfig;
import com.sharecare.cms.articles.configuration.RemoteServerResourceConfig;
import com.sharecare.cms.articles.schema.ArticleJCRSchema;
import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

@Slf4j
class RemoteArticlePublisher implements RemoteDataPublisher {

	static final String NODE_TYPE = "mgnl:article";

	private final Map<String, ArticlesApiClient> clientMap;
	private final ArticleRequestBuilder articleRequestBuilder;
	private final ArticleAssetProcessor articleAssetProcessor;

	@Inject
	public RemoteArticlePublisher(ArticlesModuleConfig articlesModuleConfig,
								  ArticleRequestBuilder articleRequestBuilder,
								  ArticleAssetProcessor articleAssetProcessor) {
		this.articleAssetProcessor = articleAssetProcessor;
		this.clientMap = buildApiClients(articlesModuleConfig.getPublishing().get(articlesModuleConfig.getEnvironment()));
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
				articleRequests.forEach((a -> a.setImageUrl(response.getUrl())));
			}

			log.debug("Executing PUT rest call to /articles ");
			BasicResponse response = client.postRequest(articleRequests).toUrl("/articles").execute();

			if (response.getStatusCode() == 200) {
				log.info("Successfully published content item {}:{} to {}", node.getPrimaryNodeType().getName(), node.getIdentifier(), environment);
				if (!activeStatusUpdater.updateStatus(node, environment, addEnvironmentCallback))
					log.error("Failed to update node status: {}", node);
			} else {
				log.error("Failed Activation on  {} . Response from service {}", environment, response.getStatusCode());
				return false;
			}
		} catch (RepositoryException e) {
			log.error("Failed Activation of article  {} ", ExceptionUtils.getFullStackTrace(e));
			return false;
		}

		return true;
	}

	@Override
	public boolean unPublish(Node node, String environment) {

		try {

			log.warn("Deleting {}:{} content from {} ", node.getPrimaryNodeType().getName(), node.getIdentifier(), environment);
			ArticlesApiClient client = clientMap.get(environment);
			List<Article> articles = articleRequestBuilder.forNode(node);
			articles.forEach(a -> {
				String deleteUri = String.format("/articles/%s", a.getId());
				log.debug("Executing DELETE rest call {}", deleteUri);
				BasicResponse response = client.deleteRequest().toUrl(deleteUri).execute();
				if (response.getStatusCode() == 200) {
					log.info("Successfully deleted content item {} from remote", a.getArticleUri());
					if (!activeStatusUpdater.updateStatus(node, environment, removeEnvironmentCallback))
						log.error("Failed to update node status: {}", node);
				}
			});
		} catch (RepositoryException e) {
			log.error("Failed De-Activation of article  {} ", ExceptionUtils.getFullStackTrace(e));
			return false;
		}

		return true;
	}


	private interface StatusUpdater<V, I, S> {

		boolean updateStatus(V valueFactory, I item, S environment);
	}

	private StatusUpdater<ValueFactory, Node, String> addEnvironmentCallback = (vf, item, environment) -> {
		try {
			if (item.hasProperty(ArticleJCRSchema.activeStatus.name())) {
				Property p = item.getProperty(ArticleJCRSchema.activeStatus.name());
				Set<Value> values = Sets.newHashSet(p.getValues());
				values.add(vf.createValue(environment));
				p.setValue(values.toArray(new Value[values.size()]));
			} else {
				Value[] values = new Value[]{vf.createValue(environment)};
				item.setProperty(ArticleJCRSchema.activeStatus.name(), values);
			}
		} catch (RepositoryException e) {
			log.error("Failed to update JCR {} ", ExceptionUtils.getFullStackTrace(e));
		}

		return true;
	};

	private StatusUpdater<ValueFactory, Node, String> removeEnvironmentCallback = (vf, item, environment) -> {
		try {
			if (item.hasProperty(ArticleJCRSchema.activeStatus.name())) {
				Property p = item.getProperty(ArticleJCRSchema.activeStatus.name());
				Set<Value> values = Sets.newHashSet(p.getValues());
				values.remove(vf.createValue(environment));
				p.setValue(values.toArray(new Value[values.size()]));
			}
		} catch (RepositoryException e) {
			log.error("Failed to update JCR {} ", ExceptionUtils.getFullStackTrace(e));
			return false;
		}

		return true;
	};


	private StatusUpdater<Node, String, StatusUpdater<ValueFactory, Node, String>> activeStatusUpdater = (item, environment, statusUpdater) -> {
		log.debug("Marking item {} as active on environment {}", item, environment);
		try {
			Session session = item.getSession();
			ValueFactory valueFactory = session.getValueFactory();
			boolean result = statusUpdater.updateStatus(valueFactory, item, environment);
			session.save();
			return result;
		} catch (javax.jcr.RepositoryException e) {
			log.error("Failed to mark as activated {} ", ExceptionUtils.getFullStackTrace(e));
			return unPublish(item, environment);
		}
	};

	private Map<String, ArticlesApiClient> buildApiClients(Map<String, RemoteServerResourceConfig> environmentsMap) {

		return environmentsMap.entrySet()
				.stream()
				.collect(Collectors.toMap(Map.Entry::getKey,
						entry -> {
							RemoteServerResourceConfig config = entry.getValue();
							BasicAuthCredentials basicAuthCredentials = new BasicAuthCredentials(entry.getValue().getUsername(), config.getPassword());
							ServerInfo serverInfo = new ServerInfo(config.getHostProtocol(), config.getHostAddress(), config.getHostPort());
							return new ArticlesApiClient(basicAuthCredentials, serverInfo);
						}));
	}

	@Override
	public boolean canService(String nodeType) {
		return NODE_TYPE.equals(nodeType);
	}
}
