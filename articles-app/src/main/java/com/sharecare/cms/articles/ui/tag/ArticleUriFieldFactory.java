package com.sharecare.cms.articles.ui.tag;

import javax.inject.Inject;

import com.vaadin.data.Item;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Field;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;

public class ArticleUriFieldFactory  extends AbstractFieldFactory<ArticleUriFieldDefinition, PropertysetItem> {

	private final TagService tagService;


	@Inject
	public ArticleUriFieldFactory(ArticleUriFieldDefinition definition,
								  Item relatedFieldItem,
								  UiContext uiContext,
								  I18NAuthoringSupport i18NAuthoringSupport,
								  TagService tagService) {
		super(definition, relatedFieldItem, uiContext, i18NAuthoringSupport);
		this.tagService = tagService;
	}


	@Override
	protected Field<PropertysetItem> createFieldComponent() {
		return new ArticleUriField(tagService);
	}
}
