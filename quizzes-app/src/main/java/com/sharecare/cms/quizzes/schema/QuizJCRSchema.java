package com.sharecare.cms.quizzes.schema;

import java.util.stream.Stream;

public enum QuizJCRSchema {
    id,
    activateSearchResults,
    assetReference,
    branding,
    code,
    disableSocial,
    frequencyOfReview,
    intro,
    metaKeywords,
    metaDescription,
    ogLabel,
    ogType,
    ogImage,
    ogTitle,
    ogDescription,
    ogUrl,
    noIndexFollow,
    canonicalReference,
    primaryTag,
    rootTag,
    firstAncestralTopic,
    secondaryTags,
    quizId,
    showShortDescription,
    segments,
    sourceType,
    subTitle,
    title,
    path,
    uri,
    questions,
    publishDate,
    caption,
    answerA,
    answerB,
    answerC,
    answerD,
    explanation,
    correctAnswer;

    public static QuizJCRSchema forName(String field){
        if( field == null )
            return null;
        return Stream.of(values()).filter(v->v.name().equals(field))
                .findFirst()
                .orElse(null);
    }
}
