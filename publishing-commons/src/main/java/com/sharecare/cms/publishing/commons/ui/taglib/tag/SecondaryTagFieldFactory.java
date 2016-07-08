package com.sharecare.cms.publishing.commons.ui.taglib.tag;

import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagService;
import com.vaadin.data.Item;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Field;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;

import javax.inject.Inject;

public class SecondaryTagFieldFactory extends AbstractFieldFactory<SecondaryTagFieldDefinition, PropertysetItem> {

	private final TagService tagService;


	@Inject
	public SecondaryTagFieldFactory(SecondaryTagFieldDefinition definition,
									Item relatedFieldItem,
									UiContext uiContext,
									I18NAuthoringSupport i18NAuthoringSupport,
									TagService tagService) {
		super(definition, relatedFieldItem, uiContext, i18NAuthoringSupport);
		this.tagService = tagService;
	}


	@Override
	protected Field<PropertysetItem> createFieldComponent() {
		return new SecondaryTagField(tagService);
	}
}
