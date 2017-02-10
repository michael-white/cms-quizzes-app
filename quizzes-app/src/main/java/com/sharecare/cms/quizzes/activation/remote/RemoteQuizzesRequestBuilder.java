package com.sharecare.cms.quizzes.activation.remote;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.sharecare.cms.cloudinary.dam.AssetUploadResult;
import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import com.sharecare.cms.quizzes.configuration.QuizzesModuleConfig;
import com.sharecare.cms.quizzes.schema.QuizJCRSchema;
import com.sharecare.quizzes.sdk.model.OpenGraph;
import com.sharecare.quizzes.sdk.model.QuestionRequest;
import com.sharecare.quizzes.sdk.model.QuizRequest;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sharecare.cms.publishing.commons.utils.NodeUtils.*;

@Slf4j
public class RemoteQuizzesRequestBuilder implements QuizzesRequestBuilder {

    private final QuizAssetProcessor quizAssetProcessor;


    @Inject
    public RemoteQuizzesRequestBuilder(CommonsModuleConfig commonsModuleConfig,
                                           QuizzesModuleConfig quizModuleConfig,
                                           QuizAssetProcessor quizAssetProcessor) {
        this.quizAssetProcessor = quizAssetProcessor;
    }

    @Override
    public QuizRequest forNode(Node node, String environment) throws RepositoryException {

        //TODO: These are all currently 'text' fields...
        //Ultimately, some may be imageUploader to use QuizAssetProcessor
        //Or Checkbox, or Radio,  or Select ... Once SDK is available,  revisit and finalize these

        Optional<AssetUploadResult> introImageUploadResult = quizAssetProcessor.uploadAssetFrom(node, QuizJCRSchema.assetReference.name());



        OpenGraph openGraph = OpenGraph.builder()
                .description(fromNode(QuizJCRSchema.ogDescription.name(), node))
                .image(fromNode(QuizJCRSchema.ogImage.name(), node))
                .label(fromNode(QuizJCRSchema.ogLabel.name(), node))
                .title(fromNode(QuizJCRSchema.ogTitle.name(), node))
                .type(fromNode(QuizJCRSchema.ogType.name(), node))
                .url(fromNode(QuizJCRSchema.ogUrl.name(), node))
                .build();


        return QuizRequest.builder()
                .id(node.getIdentifier())
                .title(fromNode(QuizJCRSchema.title.name(), node))
                .subTitle(fromNode(QuizJCRSchema.subTitle.name(), node))
              //  .path(fromNode(QuizJCRSchema.path.name(), node))
             //   .uri(fromNode(QuizJCRSchema.uri.name(), node))
             //   .publishDate(Integer.parseInt(fromNode(QuizJCRSchema.publishDate.name(), node)))
                .branding(fromNode(QuizJCRSchema.branding.name(),node))
                .keywords(fromCSV(fromNode(QuizJCRSchema.metaKeywords.name(),node)))
                .primaryTag(extractTag(QuizJCRSchema.primaryTag.name(), node))
              //  .rootTag(fromNode(QuizJCRSchema.rootTag.name(), node))
              //  .firstAncestralTopic(fromNode(QuizJCRSchema.firstAncestralTopic.name(), node))
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
                    .explantion(fromNode(QuizJCRSchema.explanation.name(), question))
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
