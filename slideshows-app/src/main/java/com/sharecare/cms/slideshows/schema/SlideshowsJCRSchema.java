package com.sharecare.cms.slideshows.schema;

public enum SlideshowsJCRSchema {
	legacyUUID,
	title,
	description,
	image,
	showAd,
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
	redirects;


	public static SlideshowsJCRSchema forName(String field) {
		for(SlideshowsJCRSchema v : values()) {
			if (v.name().equals(field))
				return v;
		}
		return null;
	}
}
