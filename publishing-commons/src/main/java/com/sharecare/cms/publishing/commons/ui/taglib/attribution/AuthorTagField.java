package com.sharecare.cms.publishing.commons.ui.taglib.attribution;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.function.Consumer;

public class AuthorTagField extends CustomField<PropertysetItem> {


    public static final String AUTHOR_ID_FIELD       = "id";
    public static final String AUTHOR_NAME_FIELD     = "name";
    public static final String AUTHOR_DATA_URI_FIELD = "dataUri";
    public static final String AUTHOR_WEB_URI_FIELD  = "webUri";

    private VerticalLayout rootLayout;

    @Override
    protected Component initContent() {

        rootLayout = new VerticalLayout();
        rootLayout.setSizeFull();
        rootLayout.setSpacing(true);

        HorizontalLayout selectMenus = initSelectAuthorComponents();
        rootLayout.addComponent(selectMenus);

        return rootLayout;
    }

    private HorizontalLayout initSelectAuthorComponents() {

        ComboBox select = initSelectMenuControl();
        HorizontalLayout selectMenuLayout = new HorizontalLayout();
        selectMenuLayout.setSizeFull();
        selectMenuLayout.addComponent(select);

        return selectMenuLayout;
    }

    private ComboBox initSelectMenuControl() {
        final BeanItemContainer<Author> container = new BeanItemContainer<Author>(Author.class);

        final ComboBox select = new ComboBox("", container);
        select.setNullSelectionAllowed(false);
        select.setItemCaptionPropertyId(AUTHOR_NAME_FIELD);
        select.setNewItemsAllowed(true);
        select.setImmediate(true);

        PropertysetItem storedData = getValue();
        if (!storedData.getItemPropertyIds().isEmpty()) {
            Author author = hydrateStoredValue(storedData);
            container.addBean(author);
            select.select(author);
        }

        select.setNewItemHandler(newItemCaption -> {
            container.addItem(new Author("1", "Dr. Amy Adelberg  - /doctor/dr-amy-adelberg", "/user/dr-amy-adelberg", "/doctor/dr-amy-adelberg"));
            container.addItem(new Author("2", "Dr. Michael Roizen - /doctor/dr-michael-roizen", "/user/dr-michael-roizen", "/doctor/dr-michael-roizen"));
            container.addItem(new Author("3", "AARP - /group/aarp", "/group/aarp", "/group/aarp"));
            select.select(new Author("1", "Dr. Amy Adelberg  - /doctor/dr-amy-adelberg", "/user/dr-amy-adelberg", "/doctor/dr-amy-adelberg"));
        });

        select.addValueChangeListener(v -> {
            Author author = (Author) v.getProperty().getValue();
            PropertysetItem propertysetItem = getValue();
            if (propertysetItem.getItemPropertyIds().isEmpty()) {
                propertysetItem.addItemProperty(AUTHOR_ID_FIELD, new ObjectProperty<String>(author.getId()));
                propertysetItem.addItemProperty(AUTHOR_NAME_FIELD, new ObjectProperty<String>(author.getName()));
                propertysetItem.addItemProperty(AUTHOR_DATA_URI_FIELD, new ObjectProperty<String>(author.getDataUri()));
                propertysetItem.addItemProperty(AUTHOR_WEB_URI_FIELD, new ObjectProperty<String>(author.getWebUri()));
            } else {
                propertysetItem.getItemProperty(AUTHOR_ID_FIELD).setValue(new ObjectProperty<String>(author.getId()));
                propertysetItem.getItemProperty(AUTHOR_NAME_FIELD).setValue(new ObjectProperty<String>(author.getName()));
                propertysetItem.getItemProperty(AUTHOR_DATA_URI_FIELD).setValue(new ObjectProperty<String>(author.getDataUri()));
                propertysetItem.getItemProperty(AUTHOR_WEB_URI_FIELD).setValue(new ObjectProperty<String>(author.getWebUri()));
            }


            getPropertyDataSource().setValue(propertysetItem);
        });
        return select;
    }


    private Author hydrateStoredValue(PropertysetItem pi) {
        return Author.builder()
                     .id(pi.getItemProperty(AUTHOR_ID_FIELD).getValue().toString())
                     .name(pi.getItemProperty(AUTHOR_NAME_FIELD).getValue().toString())
                     .dataUri(pi.getItemProperty(AUTHOR_DATA_URI_FIELD).getValue().toString())
                     .webUri(pi.getItemProperty(AUTHOR_WEB_URI_FIELD).toString())
                     .build();
    }

    @Override
    public Class<? extends PropertysetItem> getType() {
        return PropertysetItem.class;
    }


    @Data
    @AllArgsConstructor
    @Builder
    public static class Author {
        String id;
        String name;
        String dataUri;
        String webUri;
    }
}
