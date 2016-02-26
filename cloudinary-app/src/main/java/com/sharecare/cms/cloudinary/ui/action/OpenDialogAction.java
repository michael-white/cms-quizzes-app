/**
 * This file Copyright (c) 2015 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This program and the accompanying materials are made
 * available under the terms of the Magnolia Network Agreement
 * which accompanies this distribution, and is available at
 * http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package com.sharecare.cms.cloudinary.ui.action;

import javax.inject.Inject;
import javax.inject.Named;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.PropertysetItem;
import info.magnolia.event.EventBus;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.api.action.ActionExecutionException;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.dialog.formdialog.FormDialogPresenter;
import info.magnolia.ui.dialog.formdialog.FormDialogPresenterFactory;
import info.magnolia.ui.form.EditorCallback;
import info.magnolia.ui.framework.action.OpenCreateDialogAction;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.vaadin.integration.jcr.DefaultProperty;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * Action that opens create dialog for the new folder in Cloudinary.
 */
public class OpenDialogAction extends OpenCreateDialogAction {

    public static final String SELECTED_ITEM = "selectedItem";

    private BeanItem<Item> parentItem;
    private final FormDialogPresenterFactory formDialogPresenterFactory;
    private final UiContext uiContext;
    private final SimpleTranslator i18n;

    @Inject
    public OpenDialogAction(OpenDialogActionDefinition definition, BeanItem<Item> parentItem, FormDialogPresenterFactory formDialogPresenterFactory, UiContext uiContext, @Named("admincentral") EventBus eventBus, ContentConnector contentConnector, SimpleTranslator i18n) {
        super(definition, parentItem, formDialogPresenterFactory, uiContext, eventBus, contentConnector, i18n);
        this.parentItem = parentItem;
        this.formDialogPresenterFactory = formDialogPresenterFactory;
        this.uiContext = uiContext;
        this.i18n = i18n;
    }

    @Override
    public void execute() throws ActionExecutionException {
        final String dialogName = getDefinition().getDialogName();
        if (StringUtils.isBlank(dialogName)) {
            uiContext.openNotification(MessageStyleTypeEnum.ERROR, false, i18n.translate("ui-framework.actions.no.dialog.definition", getDefinition().getName()));
            return;
        }

        final FormDialogPresenter formDialogPresenter = formDialogPresenterFactory.createFormDialogPresenter(dialogName);

        if (formDialogPresenter == null) {
            uiContext.openNotification(MessageStyleTypeEnum.ERROR, false, i18n.translate("ui-framework.actions.dialog.not.registered", dialogName));
            return;
        }
        Item newItem = new PropertysetItem();
        newItem.addItemProperty(SELECTED_ITEM, new DefaultProperty<>(parentItem.getBean()));
        formDialogPresenter.start(newItem, getDefinition().getDialogName(), uiContext, new EditorCallback() {

            @Override
            public void onSuccess(String actionName) {
                formDialogPresenter.closeDialog();
            }

            @Override
            public void onCancel() {
                formDialogPresenter.closeDialog();
            }
        });
    }
}
