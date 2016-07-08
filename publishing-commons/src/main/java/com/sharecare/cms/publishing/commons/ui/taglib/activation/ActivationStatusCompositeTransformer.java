package com.sharecare.cms.publishing.commons.ui.taglib.activation;

import com.google.common.collect.Lists;
import com.vaadin.data.Item;
import com.vaadin.data.util.PropertysetItem;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.transformer.basic.BasicTransformer;

import java.util.List;

public class ActivationStatusCompositeTransformer extends BasicTransformer<PropertysetItem> {

	private List<String> fieldsName = Lists.newArrayList(EnvironmentActivationField.ACTIVE_STATUS_FIELD);

	public ActivationStatusCompositeTransformer(Item relatedFormItem, ConfiguredFieldDefinition definition, Class<PropertysetItem> type, I18NAuthoringSupport i18NAuthoringSupport) {
		super(relatedFormItem, definition, type, i18NAuthoringSupport);
	}

	@Override
	public void writeToItem(PropertysetItem newValues) {
		newValues.getItemPropertyIds()
				.iterator()
				.forEachRemaining(p -> relatedFormItem.addItemProperty(p, newValues.getItemProperty(p)));
	}

	@Override
	public PropertysetItem readFromItem() {
		PropertysetItem newValues = new PropertysetItem();

		fieldsName.forEach(f -> {
			if (relatedFormItem.getItemProperty(f) != null)
				newValues.addItemProperty(f, relatedFormItem.getItemProperty(f));
		});

		return newValues;
	}
}
