package com.sharecare.cms.publishing.commons.ui.taglib.attribution;

import com.vaadin.data.Item;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Field;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;

import javax.inject.Inject;

public class AuthorTagFieldFactory extends AbstractFieldFactory<AuthorTagFieldDefinition, PropertysetItem> {


	@Inject
	public AuthorTagFieldFactory(AuthorTagFieldDefinition definition,
								 Item relatedFieldItem,
								 UiContext uiContext,
								 I18NAuthoringSupport i18NAuthoringSupport) {
		super(definition, relatedFieldItem, uiContext, i18NAuthoringSupport);
	}


	@Override
	protected Field<PropertysetItem> createFieldComponent() {
		return new AuthorTagField();
	}
}
