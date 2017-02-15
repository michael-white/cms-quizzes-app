package com.sharecare.cms.quizzes.activation.remote;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sharecare.cms.cloudinary.dam.AssetUploadResult;
import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.RemoteTagService;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagResult;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagService;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TopicResult;
import com.sharecare.cms.quizzes.configuration.QuizzesModuleConfig;
import com.sharecare.cms.quizzes.schema.QuizJCRSchema;
import com.sharecare.quizzes.sdk.model.QuestionRequest;
import com.sharecare.quizzes.sdk.model.QuizIntro;
import com.sharecare.quizzes.sdk.model.QuizRequest;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sharecare.cms.publishing.commons.utils.NodeUtils.*;

@Slf4j
public class RemoteQuizzesRequestBuilder implements QuizzesRequestBuilder {

    private final QuizAssetProcessor quizAssetProcessor;
    private final RemoteTagService remoteTagService;


    @Inject
    public RemoteQuizzesRequestBuilder(CommonsModuleConfig commonsModuleConfig,
                                           QuizzesModuleConfig quizModuleConfig,
                                           QuizAssetProcessor quizAssetProcessor,
                                           RemoteTagService remoteTagService) {
        this.quizAssetProcessor = quizAssetProcessor;
        this.remoteTagService = remoteTagService;
    }

    @Override
    public QuizRequest forNode(Node node, String environment) throws RepositoryException {


        String primaryId = fromNode(QuizJCRSchema.primaryTag.name(), node);
        List<TagResult> allParents = remoteTagService.getParentsForTag(primaryId);
        String rootTagId = primaryId;
        if(allParents.size() > 0) {
            rootTagId = allParents.get(0).getId();
        }

        TopicResult topicResult = remoteTagService.getTopicForTag(primaryId);

        Optional<AssetUploadResult> introImageResult = quizAssetProcessor.uploadAssetFrom(node, QuizJCRSchema.introImage.name());
        QuizIntro quizIntro = QuizIntro.builder()
                                        .quizImage(introImageResult.isPresent() ? introImageResult.get().getUrl() : "")
                                        .text(fromNode(QuizJCRSchema.introText.name(),node))
                                        .build();


        return QuizRequest.builder()
                .id(node.getIdentifier())
                .title(fromNode(QuizJCRSchema.title.name(), node))
                .subTitle(fromNode(QuizJCRSchema.subTitle.name(), node))
                .path(node.getPath())
                .uri(node.getName())
                .publishDate(new Date().getTime())
                .branding(fromNode(QuizJCRSchema.branding.name(),node))
                .keywords(fromCSV(fromNode(QuizJCRSchema.keywords.name(),node)))
                .primaryTag(primaryId)
                .rootTag(rootTagId)
                .firstAncestralTopic(null != topicResult ? topicResult.getId() : "")
                .topicUri(null != topicResult ?  topicResult.getUri() : "")
                .questions(processQuestions(node))
                .quizIntro(quizIntro)
                .build();
    }



    private List<String> fromCSV(String csv) {
        return Stream.of(csv.trim().split(",")).map(String::trim).collect(Collectors.toList());
    }

    private List<QuestionRequest> processQuestions(Node node) throws RepositoryException {

        List<QuestionRequest> questions = new ArrayList<>();

        if (!node.hasNode(QuizJCRSchema.questions.name()))
            return Collections.emptyList();


        NodeIterator iterator = node.getNode(QuizJCRSchema.questions.name()).getNodes();
        while (iterator.hasNext()) {

            Node question = iterator.nextNode();

            QuestionRequest.QuestionRequestBuilder questionBuilder = QuestionRequest.builder()
                    .text(fromNode(QuizJCRSchema.text.name(), question))
                    .correctAnswer(fromNode(QuizJCRSchema.correctAnswer.name(), question))
                    .explanation(fromNode(QuizJCRSchema.explanation.name(), question))
                    .notes(fromNode(QuizJCRSchema.notes.name(), question))
                    .answerA(fromNode(QuizJCRSchema.answerA.name(),question))
                    .answerB(fromNode(QuizJCRSchema.answerB.name(),question))
                    .answerC(fromNode(QuizJCRSchema.answerC.name(),question))
                    .answerD(fromNode(QuizJCRSchema.answerD.name(),question));


            questions.add(questionBuilder.build());
        }

        return questions;
    }


}
