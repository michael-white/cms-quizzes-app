package com.sharecare.cms.articles.schema;

public enum ArticleJCRSchema {
	topicUri,
	title,
	subHead,
	byline,
	bylineUrl,
	bylineUrlOptionSelect,
	realAgeOptionSelect,
	callOutBody,
	body,
	playerId,
	videoId,
	videoTitle,
	videoTeaser,
	metaKeywords,
	pageAndMetaTitle,
	metaDescription,
	hasSynviscComScore,
	ogLabel,
	disableSocial,
	ogType,
	ogImage,
	ogTitle,
	ogDescription,
	ogUrl,
	noIndexFollow,
	canonicalReference,
	imageUpload,
	contentFlags,
	propensityScore,
	expirationDate,
	livingInTheGreenScale,
	segmentSelect,
	mentions,
	authors,
	redirects;

	public static ArticleJCRSchema forName(String field) {
		for(ArticleJCRSchema v : values()) {
			if (v.name().equals(field))
				return v;
		}
		return null;
	}
}
