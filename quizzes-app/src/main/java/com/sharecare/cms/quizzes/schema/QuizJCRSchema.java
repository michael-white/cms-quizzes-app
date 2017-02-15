package com.sharecare.cms.quizzes.schema;

import java.util.stream.Stream;

public enum QuizJCRSchema {
    id,
    assetReference,
    branding,
    introText,
    introImage,
    keywords,
    primaryTag,
    rootTag,
    firstAncestralTopic,
    segments,
    subTitle,
    title,
    path,
    uri,
    questions,
    publishDate,
    text,
    answerA,
    answerB,
    answerC,
    answerD,
    explanation,
    correctAnswer,
    notes;

    public static QuizJCRSchema forName(String field){
        if( field == null )
            return null;
        return Stream.of(values()).filter(v->v.name().equals(field))
                .findFirst()
                .orElse(null);
    }
}
