package com.sharecare.cms.slideshows.ui.taglib;

import com.vaadin.data.Item;
import com.vaadin.data.util.PropertysetItem;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.transformer.basic.BasicTransformer;

public class SlideTagCompositeTransformer extends BasicTransformer<PropertysetItem> {

    public SlideTagCompositeTransformer(Item relatedFormItem, ConfiguredFieldDefinition definition, Class<PropertysetItem> type, I18NAuthoringSupport i18NAuthoringSupport) {
        super(relatedFormItem, definition, type, i18NAuthoringSupport);
    }
}
