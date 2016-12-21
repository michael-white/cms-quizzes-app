package com.sharecare.cms.publishing.commons.ui.taglib.activation;

import com.sharecare.cms.publishing.commons.configuration.CommonsModuleConfig;
import com.vaadin.data.Item;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Field;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;

import javax.inject.Inject;
import java.util.Map;

public class EnvironmentActivationFieldFactory extends AbstractFieldFactory<EnvironmentActivationDefinition, PropertysetItem> {


	private final JcrItemAdapter currentItem;
	private final Map<String, String> webHosts;

	@Inject
	public EnvironmentActivationFieldFactory(EnvironmentActivationDefinition definition,
											 Item relatedFieldItem,
											 UiContext uiContext,
											 I18NAuthoringSupport i18NAuthoringSupport,
											 CommonsModuleConfig commonsModuleConfig) {
		super(definition, relatedFieldItem, uiContext, i18NAuthoringSupport);
		this.currentItem = (JcrItemAdapter)relatedFieldItem;
		this.webHosts = commonsModuleConfig.getWebHost();
	}


	@Override
	protected Field<PropertysetItem> createFieldComponent() {
		return new EnvironmentActivationField(currentItem, webHosts, definition.getWebUriPattern());
	}
}
