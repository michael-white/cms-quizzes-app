package com.sharecare.cms.guides.activation.publishing;

import com.sharecare.cms.publishing.commons.configuration.ComponentBindingConfigurer;

/**
 * Created by robert.davis on 12/20/2016.
 */
public class HealthGuidesPublishingBindingConfigurer extends ComponentBindingConfigurer  {

    @Override
    protected void configureActions() {
        bindPublisher().to(RemoteHealthGuidePublisher.class);
    }
}
