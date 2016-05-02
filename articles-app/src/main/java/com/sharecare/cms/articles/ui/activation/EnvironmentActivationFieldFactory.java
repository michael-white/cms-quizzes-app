package com.sharecare.cms.articles.ui.activation;

import javax.inject.Inject;

import java.util.Map;

import com.sharecare.cms.articles.configuration.ArticlesModuleConfig;
import com.vaadin.data.Item;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Field;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;

public class EnvironmentActivationFieldFactory extends AbstractFieldFactory<EnvironmentActivationDefinition, PropertysetItem> {


	private final JcrItemAdapter currentItem;
	private final Map<String, String> webHosts;

	@Inject
	public EnvironmentActivationFieldFactory(EnvironmentActivationDefinition definition,
											 Item relatedFieldItem,
											 UiContext uiContext,
											 I18NAuthoringSupport i18NAuthoringSupport,
											 ArticlesModuleConfig articlesModuleConfig) {
		super(definition, relatedFieldItem, uiContext, i18NAuthoringSupport);
		this.currentItem = (JcrItemAdapter)relatedFieldItem;
		this.webHosts = articlesModuleConfig.getWebHost();
	}


	@Override
	protected Field<PropertysetItem> createFieldComponent() {
		return new EnvironmentActivationField(currentItem, webHosts);
	}
}
