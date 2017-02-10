package com.sharecare.cms.quizzes.activation.publishing;

import com.sharecare.cms.publishing.commons.activation.RemoteServiceResponseProcessor;
import com.sharecare.cms.publishing.commons.activation.ServiceResponseProcessor;
import com.sharecare.cms.publishing.commons.configuration.ComponentBindingConfigurer;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.RemoteTagService;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagService;
import com.sharecare.cms.quizzes.activation.remote.QuizAssetProcessor;
import com.sharecare.cms.quizzes.activation.remote.QuizzesRequestBuilder;
import com.sharecare.cms.quizzes.activation.remote.RemoteQuizzesRequestBuilder;

public class QuizzesPublishingBindingConfigurer extends ComponentBindingConfigurer {

    @Override
    protected void configureActions() {
        bindPublisher().to(RemoteQuizzesPublisher.class);
        bindPublisher().to(RemoteQuizzesFolderPublisher.class);

        binder().bind(QuizzesRequestBuilder.class).to(RemoteQuizzesRequestBuilder.class);
        binder().bind(QuizAssetProcessor.class).to(CloudinaryQuizzesAssetProcessor.class);
        binder().bind(TagService.class).to(RemoteTagService.class);
        binder().bind(ServiceResponseProcessor.class).to(RemoteServiceResponseProcessor.class);
    }
}
