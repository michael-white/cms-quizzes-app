package com.sharecare.cms.articles.activation.remote;

import static java.util.stream.Collectors.*;

import javax.jcr.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.sharecare.articles.sdk.model.Article;
import com.sharecare.articles.sdk.model.Tag;
import com.sharecare.cms.articles.schema.ArticleJCRSchema;

public class RemoteArticleRequestBuilder implements ArticleRequestBuilder {

	private static final Pattern LOCALE_PATTERN = Pattern.compile("(\\w{2})_(\\w+)");

	private enum Locale {
		en, es
	}

	@Override
	public List<Article> forNode(Node node) throws RepositoryException {
		Map<String, Article.ArticleBuilder> localeArticles = initArticleLocaleMap(node);
		PropertyIterator it = node.getProperties();
		while (it.hasNext()) {
			Property p = it.nextProperty();
			final String field = p.getName();
			Matcher m = LOCALE_PATTERN.matcher(field);

			if (p.isMultiple()) {
				Value[] values = p.getValues();
				List<String> valueList = new ArrayList<>(values.length);
				for (Value v : values)
					valueList.add(v.getString());

				if (m.find()) {
					Article.ArticleBuilder builder = localeArticles.get(m.group(1));
					populateBuilderMulti(builder, m.group(2), valueList);
				} else {
					localeArticles.forEach((k, v) -> populateBuilderMulti(v, field, valueList));
				}
			} else {
				final String value = p.getString();
				if (m.find()) {
					Article.ArticleBuilder builder = localeArticles.get(m.group(1));
					populateBuilder(builder, m.group(2), value);
				} else {
					localeArticles.forEach((k, v) -> populateBuilder(v, field, value));
				}
			}
		}

		return localeArticles.values()
				.stream()
				.map(Article.ArticleBuilder::createArticle)
				.collect(toList());
	}

	private Map<String, Article.ArticleBuilder> initArticleLocaleMap(Node node) throws RepositoryException {

		Map<String, Article.ArticleBuilder> map = Maps.newHashMap();

		for (Locale l : Locale.values()) {
			map.put(l.name(), new Article.ArticleBuilder()
					.setId(node.getIdentifier())
					.setArticleUri(node.getName())
					.setLocale(l.name()));
		}
		return map;
	}

	private void populateBuilder(Article.ArticleBuilder builder, String field, String value) {

		ArticleJCRSchema fieldName = ArticleJCRSchema.forName(field);
		if (fieldName == null) return;

		switch (fieldName) {
			case body:
				builder.setBody(value);
			case title:
				builder.setBody(value);
			case subHead:
				builder.setSubHead(value);
			case bylineUrl:
				builder.setByLineUri(value);
			case byline:
				builder.setByLine(value);
			case bylineUrlOptionSelect:
				builder.setByLineOption(value);
			case realAgeOptionSelect:
				builder.setRealAge(Boolean.valueOf(value));
			case callOutBody:
				builder.setCallOutBody(value);
			case videoId:
				builder.setVideoId(value);
			case playerId:
				builder.setByLine(value);
			case videoTitle:
				builder.setVideoTitle(value);
			case videoTeaser:
				builder.setVideoTeaser(value);
			case pageAndMetaTitle:
				builder.setMetaTitle(Collections.singletonList(value));
			case metaDescription:
				builder.setMetaDescription(Collections.singletonList(value));
			case metaKeywords:
				builder.setKeywords(Splitter.on(",").splitToList(value));
			case hasSynviscComScore:
				builder.setHasSynviscComScore(Boolean.valueOf(value));
			case ogLabel:
				builder.setOgLabel(value);
			case disableSocial:
				builder.setDisableSocialButtons(Boolean.valueOf(value));
			case ogType:
				builder.setOgType(value);
			case ogImage:
				builder.setOgImage(value);
			case ogTitle:
				builder.setOgTitle(value);
			case ogDescription:
				builder.setOgDescription(value);
			case ogUrl:
				builder.setOgUrl(value);
			case noIndexFollow:
				builder.setNoIndexFollow(Boolean.valueOf(value));
			case canonicalReference:
				builder.setCanonicalReference(value);
			case primaryTag:
				builder.setPrimaryTag(new Tag(value, "tag"));
		}
	}

	private void populateBuilderMulti(Article.ArticleBuilder builder, String field, List<String> values) {
		if (field.equals(ArticleJCRSchema.segmentSelect.name()))
			builder.setSegments(values);

		else if (field.equals(ArticleJCRSchema.secondaryTag.name()))
			builder.setSecondaryTags(values.stream().map(v -> new Tag(v, "tag")).collect(Collectors.toList()));
	}
}


