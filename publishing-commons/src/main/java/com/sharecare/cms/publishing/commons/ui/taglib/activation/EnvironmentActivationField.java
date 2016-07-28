package com.sharecare.cms.publishing.commons.ui.taglib.activation;

import com.vaadin.data.Property;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class EnvironmentActivationField  extends CustomField<PropertysetItem> {
	
	public static final String ACTIVE_STATUS_FIELD = "activeStatus";
	private final String webUriField;

	private  JcrItemAdapter currentItem;
	private  Map<String, String> webHosts;

	public EnvironmentActivationField(JcrItemAdapter currentItem, Map<String, String> webHosts, String webUriField) {
		super();
		this.currentItem = currentItem;
		this.webHosts = webHosts;
		this.webUriField = webUriField;
	}

	@Override
	protected Component initContent() {

		VerticalLayout rootLayout = new VerticalLayout();
		rootLayout.setSizeFull();
		rootLayout.setSpacing(true);

		PropertysetItem savedValues = getValue();
		if (savedValues != null) {
			Property savedProperty = savedValues.getItemProperty(ACTIVE_STATUS_FIELD);

			if (savedProperty != null && savedProperty.getValue() instanceof List) {
				List<String> envList = (List<String>)savedProperty.getValue();
				envList.forEach(v -> rootLayout.addComponent(generateLink(v)));
			}
		}

		return rootLayout;
	}

	private Component generateLink(String environment) {
		String host = webHosts.get(environment);
		String articleUri = getCurrentItem().getItemProperty(webUriField).toString();
		Link link = new Link(environment.toUpperCase(), new ExternalResource(host + articleUri));
		link.setTargetName("_blank");
		return link;
	}

	@Override
	public Class<? extends PropertysetItem> getType() {
		return PropertysetItem.class;
	}
}
