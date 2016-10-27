package com.sharecare.cms.slideshows.schema;

public enum SlideshowsJCRSchema {
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
	redirects;


	public static SlideshowsJCRSchema forName(String field) {
		for(SlideshowsJCRSchema v : values()) {
			if (v.name().equals(field))
				return v;
		}
		return null;
	}
}
