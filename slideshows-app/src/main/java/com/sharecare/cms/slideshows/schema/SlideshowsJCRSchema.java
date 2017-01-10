package com.sharecare.cms.slideshows.schema;

public enum SlideshowsJCRSchema {
	legacyUUID,
	title,
	description,
	image,
	showAd,
	byline,
	bylineUrl,
	bylineUrlOptionSelect,
	slides,
	pageAndMetaTitle,
	metaDescription,
	metaKeywords,
	disableSocial,
	segmentSelect,
	mentions,
	authors,
	topicUri,
	primaryTag,
	secondaryTag,
	slideTitle,
	slideDescription,
	redirects,
	ogLabel,
	ogType,
	ogImage,
	ogTitle,
	ogDescription,
	ogUrl,
	noIndexFollow,
	canonicalReference,
    contentFlags,
    propensityScore,
    expirationDate,
    livingInTheGreenScale;


	public static SlideshowsJCRSchema forName(String field) {
		for(SlideshowsJCRSchema v : values()) {
			if (v.name().equals(field))
				return v;
		}
		return null;
	}
}
