package com.sharecare.cms.articles.activation;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class ArticleRequestFactory {

	public static ArticleRequest instance(Node node) throws RepositoryException {
		return  ArticleRequest.builder()
				.uuid(node.getIdentifier())
				.title(readValue("title", node))
				.content(readValue("description", node))
				.build();
	}

	private static String readValue(String key, Node node) throws RepositoryException {
		return (node.hasProperty(key)) ? node.getProperty(key).getString() : ""; // TODO - handle nonString values
	}


	// TODO Replace with the ArticleAPI SDK
	@Getter
	@Setter
	@AllArgsConstructor
	@Builder
	public static class ArticleRequest {
		public String uuid;
		public String title;
		public String content;


	}

}


