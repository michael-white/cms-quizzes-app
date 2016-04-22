package com.sharecare.cms.articles.ui.activation;

import javax.inject.Inject;

import com.sharecare.cms.articles.ui.tag.ArticleUriField;
import com.sharecare.cms.articles.ui.tag.ArticleUriFieldDefinition;
import com.sharecare.cms.articles.ui.tag.TagService;
import com.vaadin.data.Item;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Field;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;

public class EnvironmentActivationFieldFactory extends AbstractFieldFactory<EnvironmentActivationDefinition, PropertysetItem> {


	@Inject
	public EnvironmentActivationFieldFactory(EnvironmentActivationDefinition definition,
											 Item relatedFieldItem,
											 UiContext uiContext,
											 I18NAuthoringSupport i18NAuthoringSupport,
											 TagService tagService) {
		super(definition, relatedFieldItem, uiContext, i18NAuthoringSupport);
	}


	@Override
	protected Field<PropertysetItem> createFieldComponent() {
		return new EnvironmentActivationField();
	}
}
