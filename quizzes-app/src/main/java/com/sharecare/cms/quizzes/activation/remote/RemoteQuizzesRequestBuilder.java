package com.sharecare.cms.quizzes.activation.remote;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sharecare.cms.cloudinary.dam.AssetUploadResult;
import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.RemoteTagService;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagService;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TopicResult;
import com.sharecare.cms.quizzes.configuration.QuizzesModuleConfig;
import com.sharecare.cms.quizzes.schema.QuizJCRSchema;
import com.sharecare.quizzes.sdk.model.OpenGraph;
import com.sharecare.quizzes.sdk.model.QuestionRequest;
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

        //TODO: These are all currently 'text' fields...
        //Ultimately, some may be imageUploader to use QuizAssetProcessor
        //Or Checkbox, or Radio,  or Select ... Once SDK is available,  revisit and finalize these

        Optional<AssetUploadResult> introImageUploadResult = quizAssetProcessor.uploadAssetFrom(node, QuizJCRSchema.assetReference.name());

       // List<Tag> allParents = remoteTagService.getAllParents(primaryTag);
        TopicResult topicResult = remoteTagService.getTopicForTag(fromNode(QuizJCRSchema.primaryTag.name(), node));

        return QuizRequest.builder()
                .id(node.getIdentifier())
                .title(fromNode(QuizJCRSchema.title.name(), node))
                .subTitle(fromNode(QuizJCRSchema.subTitle.name(), node))
                .path(fromNode(QuizJCRSchema.path.name(), node))
                .uri(fromNode(QuizJCRSchema.uri.name(), node))
                .publishDate(new Date().getTime())
                .branding(fromNode(QuizJCRSchema.branding.name(),node))
                .keywords(fromCSV(fromNode(QuizJCRSchema.metaKeywords.name(),node)))
                .primaryTag(fromNode(QuizJCRSchema.primaryTag.name(), node))
                .rootTag(fromNode(QuizJCRSchema.primaryTag.name(), node))
                .firstAncestralTopic(topicResult.getId())
                .questions(processQuestions(node))
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
                    .caption(fromNode(QuizJCRSchema.caption.name(), question))
                    .correctAnswer(fromNode(QuizJCRSchema.correctAnswer.name(), question))
                    .explanation(fromNode(QuizJCRSchema.explanation.name(), question))
                    .notes(fromNode(QuizJCRSchema.notes.name(), question))
                    .answerA(fromNode(QuizJCRSchema.answerA.name(),question))
                    .answerB(fromNode(QuizJCRSchema.answerB.name(),question))
                    .answerC(fromNode(QuizJCRSchema.answerC.name(),question))
                    .answerD(fromNode(QuizJCRSchema.answerD.name(),question))
            ;


            questions.add(questionBuilder.build());
        }

        return questions;
    }


}
