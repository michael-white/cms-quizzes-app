package com.sharecare.cms.publishing.commons.ui.taglib.tag;

import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagService;
import com.vaadin.data.Item;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Field;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import javax.inject.Inject;

public class PrimaryTagFieldFactory extends AbstractFieldFactory<PrimaryTagFieldDefinition, PropertysetItem> {

	private final TagService     tagService;
	private final JcrNodeAdapter currentItem;


	@Inject
	public PrimaryTagFieldFactory(PrimaryTagFieldDefinition definition,
								  Item relatedFieldItem,
								  UiContext uiContext,
								  I18NAuthoringSupport i18NAuthoringSupport,
								  TagService tagService) {
		super(definition, relatedFieldItem, uiContext, i18NAuthoringSupport);
		this.tagService = tagService;
		this.currentItem = (JcrNodeAdapter) relatedFieldItem;
	}


	@Override
	protected Field<PropertysetItem> createFieldComponent() {
		return new PrimaryTagField(tagService, currentItem);
	}
}
