package com.sharecare.cms.guides.schema;

import java.util.stream.Stream;

public enum HealthGuideJCRSchema {

    id,
    title,
    description,
    sponsorName,
    tocImageUrl,
    thumbnailUrl,
    healthGuideUri,
    primaryTag,
    secondaryTags,
    keywords,
    segments,
    contentRefList,
    image,
    topicUri,
    ogLabel,
    ogType,
    ogImage,
    ogTitle,
    ogDescription,
    ogUrl,
    noIndexFollow,
    canonicalReference,
    pageAndMetaTitle,
    metaDescription,
    disableSocial,
    publishDate,
    creationDate;

    public static HealthGuideJCRSchema forName(String field){
        if( field == null )
            return null;
        return Stream.of(values()).filter(v->v.name().equals(field))
                                  .findFirst()
                                  .orElse(null);
    }
}
