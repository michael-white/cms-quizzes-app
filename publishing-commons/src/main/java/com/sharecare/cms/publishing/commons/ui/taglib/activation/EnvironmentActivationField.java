package com.sharecare.cms.publishing.commons.ui.taglib.activation;

import com.sharecare.cms.publishing.commons.ui.taglib.tag.PrimaryTagField;
import com.vaadin.data.Property;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.*;
import info.magnolia.ui.vaadin.integration.jcr.JcrItemAdapter;
import lombok.Getter;

import javax.jcr.RepositoryException;
import java.util.List;
import java.util.Map;

@Getter
public class EnvironmentActivationField  extends CustomField<PropertysetItem> {
	
	public static final String ACTIVE_STATUS_FIELD = "activeStatus";
	private final String webUriPattern;

	private  JcrItemAdapter currentItem;
	private  Map<String, String> webHosts;

	public EnvironmentActivationField(JcrItemAdapter currentItem, Map<String, String> webHosts, String webUriPattern) {
		super();
		this.currentItem = currentItem;
		this.webHosts = webHosts;
		this.webUriPattern = webUriPattern;
	}

	@Override
	protected Component initContent() {

		VerticalLayout rootLayout = new VerticalLayout();
		rootLayout.setSizeFull();
		rootLayout.setSpacing(true);

		PropertysetItem savedValues = getValue();

        Object activationStatus = getCurrentItem().getItemProperty("mgnl:activationStatus");

		if (savedValues != null && activationStatus != null && Boolean.TRUE.equals(activationStatus)) {
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
		try {
			String contentUri = getCurrentItem().getJcrItem().getName();
			String topicUri = getOrEmpty(PrimaryTagField.TOPIC_URI_FIELD);

			String webUri = webUriPattern.replaceAll("\\{topic\\}", topicUri).replaceAll("\\{uri\\}", contentUri);
			Link link = new Link(environment.toUpperCase(), new ExternalResource(host + webUri));
			link.setTargetName("_blank");
			return link;
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

		return new Label();
	}

	private String getOrEmpty(String fieldName) {
		Property p = getCurrentItem().getItemProperty(fieldName);
		if (p != null) return p.getValue().toString();

		return "";
	}

	@Override
	public Class<? extends PropertysetItem> getType() {
		return PropertysetItem.class;
	}
}
