package com.sharecare.cms.publishing.commons.ui.taglib.segmentation;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.vaadin.data.Item;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.definition.OptionGroupFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import info.magnolia.ui.form.field.factory.OptionGroupFieldFactory;

import java.util.List;

public class SegmentOptionGroupFieldFactory extends OptionGroupFieldFactory<OptionGroupFieldDefinition> {

    @Inject
    public SegmentOptionGroupFieldFactory(OptionGroupFieldDefinition definition, Item relatedFieldItem, UiContext uiContext, I18NAuthoringSupport i18nAuthoringSupport, ComponentProvider componentProvider) {
        super(definition, relatedFieldItem, uiContext, i18nAuthoringSupport, componentProvider);
    }

    @Override
    public List<SelectFieldOptionDefinition> getSelectFieldOptionDefinition() {

        // TODO Integrate City Service here

        SelectFieldOptionDefinition global = new SelectFieldOptionDefinition();
        global.setValue("global");
        global.setLabel("global");
        global.setSelected(true);

        SelectFieldOptionDefinition sc = new SelectFieldOptionDefinition();
        sc.setValue("global>sc");
        sc.setLabel("global>sc");

        SelectFieldOptionDefinition hca = new SelectFieldOptionDefinition();
        hca.setValue("global>hca");
        hca.setLabel("global>hca");

        return Lists.newArrayList(global, sc, hca);
    }
}
