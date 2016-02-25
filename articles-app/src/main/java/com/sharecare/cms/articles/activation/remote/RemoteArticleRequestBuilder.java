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

	@Override
	public List<Article> forNode(Node node) throws RepositoryException {
		Map<String, ArticleBuilder> localeArticles = initArticleLocaleMap(node);
		PropertyIterator it = node.getProperties();
		while (it.hasNext()) {
			Property p = it.nextProperty();
			if (!p.isMultiple()) { // TODO handle this
				final String field = p.getName();
				final String value = p.getString();

				Matcher m = LOCALE_PATTERN.matcher(field);
				if (m.find()) {
					ArticleBuilder builder = localeArticles.get(m.group(1));
					populateBuilder(builder,  m.group(2), value);
				} else {
					localeArticles.forEach((k, v) -> populateBuilder(v, field, value));
				}
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
					.setArticleUri(node.getName().replaceAll("\\s", "-").toLowerCase())
					.setLocale(l.name())
					.setPublishDate(new Date().getTime()));
		}

		return map;
	}


	private void populateBuilder(ArticleBuilder builder, String field, String value)  {

		if (field.equals(ArticleJCRSchema.body.name()))
			builder.setBody(value);

		else if (field.equals((ArticleJCRSchema.title.name())))
			builder.setTitle(value);

		else if (field.equals((ArticleJCRSchema.byline.name())))
			builder.setByLine(value);

		else if (field.equals((ArticleJCRSchema.videoId.name())))
			builder.setByLine(value);

		else if (field.equals((ArticleJCRSchema.playerId.name())))
			builder.setByLine(value);

		else if (field.equals((ArticleJCRSchema.metaKeywords.name())))
			builder.setKeywords(Splitter.on(",").splitToList(value));
	}

}


