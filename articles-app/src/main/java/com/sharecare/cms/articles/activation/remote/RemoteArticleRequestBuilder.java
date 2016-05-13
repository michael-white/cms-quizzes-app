package com.sharecare.cms.articles.activation.remote;

import static java.util.stream.Collectors.*;

import javax.jcr.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
					.setArticleUri(singleValueFromNode(node, ArticleJCRSchema.articleUri))
					.setLocale(l.name())
					.setCreateDate(createdDate(node))
					.setPublishDate(new Date().getTime()));
		}

		return map;
	}

	private String singleValueFromNode(Node node, ArticleJCRSchema schema) throws RepositoryException {
		return node.getProperty(schema.name()).getString();
	}

	private Long createdDate(Node node) throws RepositoryException {
		String createdTime = node.getProperty("jcr:created").getString();
		LocalDateTime ldt = LocalDateTime.parse(createdTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		Instant instant = ldt.atZone(ZoneId.systemDefault()).toInstant();
		return instant.getEpochSecond();
	}

	private void populateBuilder(Article.ArticleBuilder builder, String field, String value) {
		if (field.equals(ArticleJCRSchema.body.name()))
			builder.setBody(value);

		else if (field.equals((ArticleJCRSchema.title.name())))
			builder.setTitle(value);

		else if (field.equals((ArticleJCRSchema.subHead.name())))
			builder.setSubHead(value);

		else if (field.equals((ArticleJCRSchema.byline.name())))
			builder.setByLine(value);

		else if (field.equals((ArticleJCRSchema.bylineUrl.name())))
			builder.setByLineUri(value);

		else if (field.equals((ArticleJCRSchema.bylineUrlOptionSelect.name())))
			builder.setByLineOption(value);

		else if (field.equals((ArticleJCRSchema.realAgeOptionSelect.name())))
			builder.setRealAge(Boolean.valueOf(value));

		else if (field.equals((ArticleJCRSchema.callOutBody.name())))
			builder.setCallOutBody(value);

		else if (field.equals((ArticleJCRSchema.videoId.name())))
			builder.setVideoId(value);

		else if (field.equals((ArticleJCRSchema.playerId.name())))
			builder.setByLine(value);

		else if (field.equals((ArticleJCRSchema.videoTitle.name())))
			builder.setVideoTitle(value);

		else if (field.equals((ArticleJCRSchema.videoTeaser.name())))
			builder.setVideoTeaser(value);

		else if (field.equals((ArticleJCRSchema.pageAndMetaTitle.name())))
			builder.setMetaTitle(Collections.singletonList(value));

		else if (field.equals((ArticleJCRSchema.metaDescription.name())))
			builder.setMetaDescription(Collections.singletonList(value));

		else if (field.equals((ArticleJCRSchema.metaKeywords.name())))
			builder.setKeywords(Splitter.on(",").splitToList(value));

		else if (field.equals((ArticleJCRSchema.hasSynviscComScore.name())))
			builder.setHasSynviscComScore(Boolean.valueOf(value));

		else if (field.equals((ArticleJCRSchema.ogLabel.name())))
			builder.setOgLabel(value);

		else if (field.equals((ArticleJCRSchema.disableSocial.name())))
			builder.setDisableSocialButtons(Boolean.valueOf(value));

		else if (field.equals((ArticleJCRSchema.ogType.name())))
			builder.setOgType(value);

		else if (field.equals((ArticleJCRSchema.ogImage.name())))
			builder.setOgImage(value);

		else if (field.equals((ArticleJCRSchema.ogTitle.name())))
			builder.setOgTitle(value);

		else if (field.equals((ArticleJCRSchema.ogDescription.name())))
			builder.setOgDescription(value);

		else if (field.equals((ArticleJCRSchema.ogUrl.name())))
			builder.setOgUrl(value);

		else if (field.equals((ArticleJCRSchema.noIndexFollow.name())))
			builder.setNoIndexFollow(Boolean.valueOf(value));

		else if (field.equals((ArticleJCRSchema.canonicalReference.name())))
			builder.setCanonicalReference(value);

		else if (field.equals(ArticleJCRSchema.primaryTag.name()))
			builder.setPrimaryTag(new Tag(value, "tag"));

	}

	private void populateBuilderMulti(Article.ArticleBuilder builder, String field, List<String> values) {
		if (field.equals(ArticleJCRSchema.segmentSelect.name()))
			builder.setSegments(values);

		else if (field.equals(ArticleJCRSchema.secondaryTag.name()))
			builder.setSecondaryTags(values.stream().map(v -> new Tag(v, "tag")).collect(Collectors.toList()));
	}

}


