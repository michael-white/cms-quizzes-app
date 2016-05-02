package com.sharecare.cms.articles.ui.tag.remote;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sharecare.cms.articles.configuration.ArticlesModuleConfig;
import com.sharecare.cms.articles.configuration.RemoteServerResourceConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

@Slf4j
public class RemoteTagService implements TagService {


	private final String TAG_RESOURCE_URI = "tag";
	private final String TOPIC_RESOURCE_URI = "topicSvc";

	private final ServerInfo serverInfo;
	private final BasicAuthCredentials basicAuthCredentials;

	@Inject
	public RemoteTagService(ArticlesModuleConfig articlesModuleConfig) {
		RemoteServerResourceConfig tagResource = articlesModuleConfig.getTagResource();
		this.serverInfo = new ServerInfo(tagResource.getHostProtocol(), tagResource.getHostAddress(), tagResource.getHostPort());
		this.basicAuthCredentials = new BasicAuthCredentials(tagResource.getUsername(), tagResource.getPassword());
	}

	@Override
	public TagResult loadByTagID(String id) {

		String requestUrl = String.format("%s/%s/%s", serverInfo.toBaseUrl(), TAG_RESOURCE_URI, id);
		BasicResponse<TagResult> response = new RESTTagTemplate().forUrl(requestUrl).getItem();
		if (response.getStatusCode() != 200)
			throw new ResourceNotFoundException("Tag resource not found " + id);

		return response.getEntity();
	}

	@Override
	public List<TagResult> searchByKeywords(String keyword) {
		String requestUrl = String.format("%s/%s?keyword=%s", serverInfo.toBaseUrl(), TAG_RESOURCE_URI, keyword);
		CollectionResponse<TagResult> results = new RESTTagTemplate().forUrl(requestUrl).getItems();
		if (results.getStatusCode() != 200)
			throw new IllegalStateException("Search for tags failed");

		return results.getEntity();
	}

	@Override
	public TopicResult getTopicForTag(String tagId) throws ResourceNotFoundException {
		String requestUrl = String.format("%s/%s?tag=%s", serverInfo.toBaseUrl(), TOPIC_RESOURCE_URI, tagId);
		BasicResponse<TopicResult> response = new RESTTopicTemplate().forUrl(requestUrl).getItem();
		if (response.getStatusCode() != 200)
			throw new ResourceNotFoundException("Topic resource not found " + tagId);

		return response.getEntity();
	}

	@Override
	public List<TagResult> getAllTopLevelTags() {
		String requestUrl = String.format("%s/%s?toplevel=true", serverInfo.toBaseUrl(), TAG_RESOURCE_URI);
		CollectionResponse<TagResult> results = new RESTTagTemplate().forUrl(requestUrl).getItems();
		if (results.getStatusCode() != 200)
			throw new IllegalStateException("Search for tags failed");

		return results.getEntity();
	}

	@Override
	public List<TagResult> getChildrenForTag(String tagId) {
		String requestUrl = String.format("%s/%s/%s/children", serverInfo.toBaseUrl(), TAG_RESOURCE_URI, tagId);
		CollectionResponse<TagResult> results =  new RESTTagTemplate().forUrl(requestUrl).getItems();
		if (results.getStatusCode() != 200)
			throw new IllegalStateException("Search for tags failed");

		return results.getEntity();
	}

	private class RESTTopicTemplate {

		String requestUrl;

		RESTTopicTemplate forUrl(String requestUrl) {
			this.requestUrl = requestUrl;
			return this;
		}

		BasicResponse<TopicResult> getItem() {
			log.debug("Executing request {}", requestUrl);

			try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

				HttpGet httpGet = new HttpGet(requestUrl);

				UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(basicAuthCredentials.getUsername(), basicAuthCredentials.getPassword());
				httpGet.addHeader(new BasicScheme().authenticate(credentials, httpGet, null));

				final ResponseHandler<BasicResponse<TopicResult>> responseHandler = response -> {
					StatusLine statusLine = response.getStatusLine();
					if (statusLine.getStatusCode() != 404) {
						HttpEntity entity = response.getEntity();
						Gson gson = new GsonBuilder().create();
						DataServerResponse result = gson.fromJson(EntityUtils.toString(entity), DataServerResponse.class);
						TopicResult tagResult = gson.fromJson(gson.toJson(result.getResponse()), TopicResult.class);

						return new BasicResponse<>(tagResult, statusLine.getStatusCode());
					} else {
						return new BasicResponse<>(null, statusLine.getStatusCode());
					}
				};
				return httpClient.execute(httpGet, responseHandler);
			} catch (IOException | AuthenticationException e) {
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
		}
	}

	private class RESTTagTemplate {
		String requestUrl;

		public RESTTagTemplate forUrl(String requestUrl) {
			this.requestUrl = requestUrl;
			return this;
		}

		public BasicResponse<TagResult> getItem() {
			log.debug("Executing request {}", requestUrl);

			try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

				HttpGet httpGet = new HttpGet(requestUrl);

				UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(basicAuthCredentials.getUsername(), basicAuthCredentials.getPassword());
				httpGet.addHeader(new BasicScheme().authenticate(credentials, httpGet, null));

				final ResponseHandler<BasicResponse<TagResult>> responseHandler = response -> {
					StatusLine statusLine = response.getStatusLine();
					if (statusLine.getStatusCode() != 404) {
						HttpEntity entity = response.getEntity();
						Gson gson = new GsonBuilder().create();
						DataServerResponse result = gson.fromJson(EntityUtils.toString(entity), DataServerResponse.class);
						TagResult tagResult = gson.fromJson(gson.toJson(result.getResponse()), TagResult.class);

						return new BasicResponse<>(tagResult, statusLine.getStatusCode());
					} else {
						return new BasicResponse<>(null, statusLine.getStatusCode());
					}
				};
				return httpClient.execute(httpGet, responseHandler);
			} catch (IOException | AuthenticationException e) {
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
		}

		public CollectionResponse<TagResult> getItems() {
			try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

				HttpGet httpGet = new HttpGet(requestUrl);

				UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(basicAuthCredentials.getUsername(), basicAuthCredentials.getPassword());
				httpGet.addHeader(new BasicScheme().authenticate(credentials, httpGet, null));

				final ResponseHandler<CollectionResponse<TagResult>> responseHandler = response -> {
					StatusLine statusLine = response.getStatusLine();
					if (statusLine.getStatusCode() != 404) {
						HttpEntity entity = response.getEntity();
						Gson gson = new GsonBuilder().create();
						DataServerResponse result = gson.fromJson(EntityUtils.toString(entity), DataServerResponse.class);

						List<TagResult> tagResults = gson.fromJson(gson.toJson(result.getResponse()), new TypeToken<List<TagResult>>() {}.getType());

						return new CollectionResponse<>(tagResults, statusLine.getStatusCode());
					} else {
						return new CollectionResponse<>(null, statusLine.getStatusCode());
					}
				};
				return httpClient.execute(httpGet, responseHandler);
			} catch (IOException | AuthenticationException e) {
				e.printStackTrace();
				throw new IllegalStateException(e);
			}
		}
	}
}
