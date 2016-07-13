package com.sharecare.cms.publishing.commons.ui.taglib.attribution;

import com.google.common.collect.Lists;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.PropertysetItem;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.transformer.basic.BasicTransformer;

import javax.jcr.Session;
import java.util.List;

public class AuthorTagFieldTransformer extends BasicTransformer<PropertysetItem> {


    public AuthorTagFieldTransformer(Item relatedFormItem, ConfiguredFieldDefinition definition, Class<PropertysetItem> type, I18NAuthoringSupport i18NAuthoringSupport) {
        super(relatedFormItem, definition, type, i18NAuthoringSupport);
    }

    @Override
    public void writeToItem(PropertysetItem newValues) {
        if (newValues.getItemProperty("author") != null)
        relatedFormItem.addItemProperty("author", newValues.getItemProperty("author"));
    }

    @Override
    public PropertysetItem readFromItem() {
        PropertysetItem newValues = new PropertysetItem();
        Property p = relatedFormItem.getItemProperty("author");
        if (p != null )
             newValues.addItemProperty("author", relatedFormItem.getItemProperty("author"));
        return newValues;
    }
}
