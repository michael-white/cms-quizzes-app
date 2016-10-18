package com.sharecare.cms.slideshows.ui.taglib;

import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.*;

public class SlideTagField extends CustomField<PropertysetItem>  {

    @Override
    protected Component initContent() {

        VerticalLayout slidePanel = new VerticalLayout();
        slidePanel.setSizeFull();

        TextField title = new TextField("Title");
        title.setSizeFull();
        slidePanel.addComponent(title);


        RichTextArea description = new RichTextArea("Description");
        title.setSizeFull();
        slidePanel.addComponent(description);

        CheckBox showAdd = new CheckBox("Show Add", true);
        slidePanel.addComponent(showAdd);


        return slidePanel;
    }

    @Override
    public Class<? extends PropertysetItem> getType() {
        return PropertysetItem.class;
    }
}
