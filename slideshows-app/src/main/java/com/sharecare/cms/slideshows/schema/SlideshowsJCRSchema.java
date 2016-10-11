package com.sharecare.cms.slideshows.schema;

public enum SlideshowsJCRSchema {
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
	segmentSelect,
	mentions,
	authors;

	public static SlideshowsJCRSchema forName(String field) {
		for(SlideshowsJCRSchema v : values()) {
			if (v.name().equals(field))
				return v;
		}
		return null;
	}
}
