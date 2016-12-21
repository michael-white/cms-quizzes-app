package com.sharecare.cms.slideshows.activation.publishing;

import com.sharecare.cms.featured.publishing.RemoteFeaturedSlideshowsPublisher;
import com.sharecare.cms.featured.remote.FeaturedSlideshowsRequestBuilder;
import com.sharecare.cms.featured.remote.RemoteFeaturedSlideshowsRequestBuilder;
import com.sharecare.cms.publishing.commons.activation.RemoteServiceResponseProcessor;
import com.sharecare.cms.publishing.commons.activation.ServiceResponseProcessor;
import com.sharecare.cms.publishing.commons.configuration.ComponentBindingConfigurer;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.RemoteTagService;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagService;
import com.sharecare.cms.slideshows.activation.remote.CloudinarySlideshowsAssetProcessor;
import com.sharecare.cms.slideshows.activation.remote.RemoteSlideshowsRequestBuilder;
import com.sharecare.cms.slideshows.activation.remote.SlideshowsAssetProcessor;
import com.sharecare.cms.slideshows.activation.remote.SlideshowsRequestBuilder;

public class SlideshowsPublishingBindingConfigurer extends ComponentBindingConfigurer {

	@Override
	protected void configureActions() {
		bindPublisher().to(RemoteSlideshowsPublisher.class);
		bindPublisher().to(RemoteSlideshowsFolderPublisher.class);

		bindPublisher().to(RemoteFeaturedSlideshowsPublisher.class);
		binder().bind(FeaturedSlideshowsRequestBuilder.class).to(RemoteFeaturedSlideshowsRequestBuilder.class);

		binder().bind(SlideshowsRequestBuilder.class).to(RemoteSlideshowsRequestBuilder.class);
		binder().bind(SlideshowsAssetProcessor.class).to(CloudinarySlideshowsAssetProcessor.class);
		binder().bind(TagService.class).to(RemoteTagService.class);
		binder().bind(ServiceResponseProcessor.class).to(RemoteServiceResponseProcessor.class);
	}
}
