package com.sharecare.cms.articles.activation.remote;

import static java.util.stream.Collectors.*;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.sharecare.articles.sdk.Article;
import com.sharecare.articles.sdk.ArticleBuilder;

public class RemoteArticleRequestBuilder implements ArticleRequestBuilder {

	private static final Pattern LOCALE_PATTERN = Pattern.compile("(\\w{2})_(\\w+)");

	private enum Locale {
		en, es
	}

	private enum ArticleModel {

		articleUri,
		title,
		subHead,
		byline,
		body,
		videoId,
		playerId,
		metaKeywords

	}


	@Override
	public List<Article> forNode(Node node) throws RepositoryException {

		Map<String, ArticleBuilder> localeArticles = initArticleLocaleMap(node);
		PropertyIterator it = node.getProperties();
		while (it.hasNext()) {
			Property p = it.nextProperty();
			if (!p.isMultiple()) { // TODO handle this
				String field = p.getName();
				String value = p.getString();
				String locale = "all";

				Matcher m = LOCALE_PATTERN.matcher(field);
				if (m.find()) {
					locale = m.group(1);
					field = m.group(2);
				}

				populateArticle(localeArticles, locale, field, value);
			}
		}

		return localeArticles.values()
				.stream()
				.map(ArticleBuilder::createArticle)
				.collect(toList());
	}

	private Map<String, ArticleBuilder> initArticleLocaleMap(Node node) throws RepositoryException {

		Map<String, ArticleBuilder> map = Maps.newHashMap();

		for (Locale l : Locale.values()) {
			map.put(l.name(), new ArticleBuilder()
					.setId(node.getIdentifier())
					.setNodeUuid(node.getIdentifier())
					.setLocale(l.name())
					.setPublishDate(new Date().getTime()));
		}

		return map;
	}

	private void populateArticle(Map<String, ArticleBuilder> localeArticles, String locale, String field, String value) throws RepositoryException {

		if (locale.equals("all")) {

			localeArticles.forEach((k, v) -> populateBuilder(v, field, value));

		} else {
			ArticleBuilder builder = localeArticles.get(locale);
			populateBuilder(builder, field, value);
		}
	}

	private void populateBuilder(ArticleBuilder builder, String field, String value)  {

		if (field.equals(ArticleModel.body.name()))
			builder.setBody(value);

		else if (field.equals(ArticleModel.articleUri.name()))
			builder.setArticleUri(value.replaceAll("\\s", "-"));

		else if (field.equals((ArticleModel.title.name())))
			builder.setTitle(value);

		else if (field.equals((ArticleModel.byline.name())))
			builder.setByLine(value);

		else if (field.equals((ArticleModel.videoId.name())))
			builder.setByLine(value);

		else if (field.equals((ArticleModel.playerId.name())))
			builder.setByLine(value);

		else if (field.equals((ArticleModel.metaKeywords.name())))
			builder.setKeywords(Splitter.on(",").splitToList(value));



	}

}


