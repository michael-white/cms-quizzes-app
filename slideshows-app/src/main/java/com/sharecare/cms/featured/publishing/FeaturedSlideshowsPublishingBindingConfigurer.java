package com.sharecare.cms.featured.publishing;

import com.sharecare.cms.featured.remote.CloudinaryFeaturedSlideshowsAssetProcessor;
import com.sharecare.cms.featured.remote.FeaturedSlideshowsAssetProcessor;
import com.sharecare.cms.featured.remote.FeaturedSlideshowsRequestBuilder;
import com.sharecare.cms.featured.remote.RemoteFeaturedSlideshowsRequestBuilder;
import com.sharecare.cms.publishing.commons.activation.RemoteServiceResponseProcessor;
import com.sharecare.cms.publishing.commons.activation.ServiceResponseProcessor;
import com.sharecare.cms.publishing.commons.configuration.ComponentBindingConfigurer;

public class FeaturedSlideshowsPublishingBindingConfigurer extends ComponentBindingConfigurer {

	@Override
	protected void configureActions() {
		bindPublisher().to(RemoteFeaturedSlideshowsPublisher.class);

		binder().bind(FeaturedSlideshowsRequestBuilder.class).to(RemoteFeaturedSlideshowsRequestBuilder.class);
		binder().bind(FeaturedSlideshowsAssetProcessor.class).to(CloudinaryFeaturedSlideshowsAssetProcessor.class);
		binder().bind(ServiceResponseProcessor.class).to(RemoteServiceResponseProcessor.class);
	}
}
