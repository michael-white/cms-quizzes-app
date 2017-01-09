package com.sharecare.cms.guides.activation.publishing;

import com.sharecare.cms.guides.activation.remote.HealthGuideAssetProcessor;
import com.sharecare.cms.guides.activation.remote.HealthGuideRequestBuilder;
import com.sharecare.cms.guides.activation.remote.RemoteHealthGuideRequestBuilder;
import com.sharecare.cms.publishing.commons.activation.RemoteServiceResponseProcessor;
import com.sharecare.cms.publishing.commons.activation.ServiceResponseProcessor;
import com.sharecare.cms.publishing.commons.configuration.ComponentBindingConfigurer;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.RemoteTagService;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagService;

/**
 * Created by robert.davis on 12/20/2016.
 */
public class HealthGuidesPublishingBindingConfigurer extends ComponentBindingConfigurer  {

    @Override
    protected void configureActions() {
        bindPublisher().to(RemoteHealthGuidePublisher.class);
        bindPublisher().to(RemoteHealthGuideFolderPublisher.class);

        binder().bind(HealthGuideRequestBuilder.class).to(RemoteHealthGuideRequestBuilder.class);
        binder().bind(HealthGuideAssetProcessor.class).to(CloudinaryHealthGuidesAssetProcessor.class);
        binder().bind(TagService.class).to(RemoteTagService.class);
        binder().bind(ServiceResponseProcessor.class).to(RemoteServiceResponseProcessor.class);
    }
}
