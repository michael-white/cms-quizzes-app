package com.sharecare.cms.articles.ui.taglib;

import com.sharecare.cms.publishing.commons.ui.taglib.tag.PrimaryTagFieldDefinition;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagService;
import com.vaadin.data.Item;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Field;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

import javax.inject.Inject;

public class ArticleUriPrimaryTagFieldFactory extends AbstractFieldFactory<PrimaryTagFieldDefinition, PropertysetItem> {

    private final TagService tagService;
    private final JcrNodeAdapter currentItem;


    @Inject
    public ArticleUriPrimaryTagFieldFactory(PrimaryTagFieldDefinition definition,
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
        return new ArticleUriPrimaryTagField(tagService, currentItem, definition.getSubdomain(), definition.getWebUriField());
    }
}
