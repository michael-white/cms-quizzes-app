package com.sharecare.cms.guides.schema;

import java.util.stream.Stream;

/**
 * Created by robert.davis on 12/20/2016.
 */
public enum HealthGuideJCRSchema {

    id,
    title,
    description,
    sponsor,
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