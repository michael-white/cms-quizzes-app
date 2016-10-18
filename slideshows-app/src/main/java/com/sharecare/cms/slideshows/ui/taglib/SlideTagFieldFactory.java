package com.sharecare.cms.slideshows.ui.taglib;

import com.vaadin.data.Item;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Field;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;

public class SlideTagFieldFactory  extends AbstractFieldFactory<SlideTagFieldDefinition, PropertysetItem> {



    public SlideTagFieldFactory(SlideTagFieldDefinition definition,
                                Item relatedFieldItem,
                                UiContext uiContext,
                                I18NAuthoringSupport i18NAuthoringSupport) {
        super(definition, relatedFieldItem, uiContext, i18NAuthoringSupport);
    }

    @Override
    protected Field<PropertysetItem> createFieldComponent() {
        return new SlideTagField();
    }
}
