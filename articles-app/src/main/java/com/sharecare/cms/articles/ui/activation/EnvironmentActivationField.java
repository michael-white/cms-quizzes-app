package com.sharecare.cms.articles.ui.activation;

import java.util.List;
import java.util.Map;

import com.sharecare.cms.articles.schema.ArticleJCRSchema;
import com.vaadin.data.Property;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;
import lombok.Getter;

@Getter
public class EnvironmentActivationField  extends CustomField<PropertysetItem> {

	private  JcrItemAdapter currentItem;
	private  Map<String, String> webHosts;

	public EnvironmentActivationField(JcrItemAdapter currentItem, Map<String, String> webHosts) {
		super();
		this.currentItem = currentItem;
		this.webHosts = webHosts;
	}

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
				envList.forEach(v -> rootLayout.addComponent(generateLink(v)));
			}
		}

		return rootLayout;
	}

	private Component generateLink(String environment) {
		String host = webHosts.get(environment);
		String articleUri = getCurrentItem().getItemProperty(ArticleJCRSchema.articleUri.name()).toString();
		Link link = new Link(environment.toUpperCase(), new ExternalResource(host + articleUri));
		link.setTargetName("_blank");
		return link;
	}

	@Override
	public Class<? extends PropertysetItem> getType() {
		return PropertysetItem.class;
	}
}
