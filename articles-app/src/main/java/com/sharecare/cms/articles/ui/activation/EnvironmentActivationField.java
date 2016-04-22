package com.sharecare.cms.articles.ui.activation;

import java.util.List;

import com.sharecare.cms.articles.schema.ArticleJCRSchema;
import com.vaadin.data.Property;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class EnvironmentActivationField  extends CustomField<PropertysetItem> {

	@Override
	protected Component initContent() {

		VerticalLayout rootLayout = new VerticalLayout();
		rootLayout.setSizeFull();
		rootLayout.setSpacing(true);

		PropertysetItem savedValues = getValue();
		if (savedValues != null) {
			Property savedProperty = savedValues.getItemProperty(ArticleJCRSchema.activeStatus.name());

			if (savedProperty != null && savedProperty.getValue() instanceof List) {
				List<String> envList = (List<String>)savedProperty.getValue();
				envList.forEach(v -> rootLayout.addComponent(new Label(v)));
			}
		}

		return rootLayout;
	}

	@Override
	public Class<? extends PropertysetItem> getType() {
		return PropertysetItem.class;
	}
}
