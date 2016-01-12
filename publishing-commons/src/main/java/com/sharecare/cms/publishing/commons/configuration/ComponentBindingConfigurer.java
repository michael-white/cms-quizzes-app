package com.sharecare.cms.publishing.commons.configuration;

import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.multibindings.Multibinder;
import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;
import info.magnolia.objectfactory.guice.AbstractGuiceComponentConfigurer;

public abstract class ComponentBindingConfigurer extends AbstractGuiceComponentConfigurer {

	private Multibinder<RemoteDataPublisher> publishersBinder;

	@Override protected void configure() {
		publishersBinder = Multibinder.newSetBinder(binder(), RemoteDataPublisher.class);
		configureActions();
	}

	/**
	 * Override this method to call {@link #bindAction}.
	 */
	protected abstract void configureActions();

	protected final LinkedBindingBuilder<RemoteDataPublisher> bindAction() {
		return publishersBinder.addBinding();
	}
}
