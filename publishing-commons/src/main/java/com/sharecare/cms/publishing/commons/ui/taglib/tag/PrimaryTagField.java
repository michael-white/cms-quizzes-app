package com.sharecare.cms.publishing.commons.ui.taglib.tag;

import com.sharecare.cms.publishing.commons.ui.taglib.tag.components.SearchFieldComponent;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.components.SearchResultsTable;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.components.SelectTagDropdown;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.ResourceNotFoundException;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagResult;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagService;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TopicResult;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.function.BiConsumer;

@Getter
public abstract class PrimaryTagField extends CustomField<PropertysetItem> {


	public static final String PRIMARY_TAG_FIELD = "primaryTag";
	public static final String PRIMARY_TAG_TITLE_FIELD = "primaryTagTitle";
	public static final String TOPIC_URI_FIELD = "topicUri";

	private static final String SELECTED_URI_LABEL = "Selected Content Uri:";
	private static final String SELECTED_PRIMARY_TAG_LABEL = "Selected Primary Tag:";

	private final TagService     tagService;
	private final JcrNodeAdapter currentItem;

	private final String contentUriJCRFieldName;

	public PrimaryTagField(TagService tagService, JcrNodeAdapter currentItem, String contentUriJCRFieldName) {
		this.tagService = tagService;
		this.currentItem = currentItem;
		this.contentUriJCRFieldName = contentUriJCRFieldName;
	}


	private VerticalLayout rootLayout;

	private Label contentUriLabel;
	private Label primaryTagLabel;

	@Override
	protected Component initContent() {

		rootLayout = new VerticalLayout();
		rootLayout.setSizeFull();
		rootLayout.setSpacing(true);

		SearchResultsTable resultsTable = new SearchResultsTable(getOnTagSelected());
		SearchFieldComponent searchFieldComponent = new SearchFieldComponent(resultsTable, tagService);
		rootLayout.addComponent(searchFieldComponent);
		rootLayout.addComponent(resultsTable);


		HorizontalLayout selectMenus = initSelectTagComponents();
		rootLayout.addComponent(selectMenus);

		initSelectedLabels();

		return rootLayout;
	}

	private void initSelectedLabels() {
		PropertysetItem savedValues = getValue();
		if (savedValues != null) {
			String contentUri = isNullOrEmpty(savedValues.getItemProperty(getContentUriJCRFieldName()));
			String primaryTag = isNullOrEmpty(savedValues.getItemProperty(PRIMARY_TAG_FIELD));
			String primaryTagTitle = isNullOrEmpty(savedValues.getItemProperty(PRIMARY_TAG_TITLE_FIELD));
			contentUriLabel = initLabel(SELECTED_URI_LABEL, contentUri, contentUriLabel);
			primaryTagLabel = initLabelPrimaryTagLabel(primaryTagTitle, primaryTag);
		}
	}

	private Label initLabelPrimaryTagLabel(String primaryTagTitle, String primaryTag) {
		return initLabel(SELECTED_PRIMARY_TAG_LABEL, primaryTagTitle + " (" + primaryTag + ")", primaryTagLabel);
	}


	private HorizontalLayout initSelectTagComponents() {
		List<TagResult> topLevelTags = getTagService().getAllTopLevelTags();
		HorizontalLayout selectMenuLayout = new HorizontalLayout();
		selectMenuLayout.addComponent(new SelectTagDropdown(topLevelTags, getOnTagSelected(), getTagService(), selectMenuLayout));

		return selectMenuLayout;
	}

	private String isNullOrEmpty(Property itemProperty) {
		return itemProperty != null ? itemProperty.toString() : StringUtils.EMPTY;
	}

	private BiConsumer<Component, TagResult> onTagSelected = (parent,tag) -> {
		PropertysetItem propertysetItem = new PropertysetItem();

		try {
			TopicResult topic = getTagService().getTopicForTag(tag.getId());
			if (topic != null) {
				String topicUri = topic.getUri();
				String contentUriFullPath = buildContentUriLabel(topicUri, getCurrentItem().getNodeName());
				initLabel(SELECTED_URI_LABEL, contentUriFullPath, contentUriLabel);
				propertysetItem.addItemProperty(TOPIC_URI_FIELD, new ObjectProperty<>(topicUri));
				propertysetItem.addItemProperty(getContentUriJCRFieldName(), new ObjectProperty<>(contentUriFullPath));
			}
		} catch (ResourceNotFoundException r) {
			// its ok. Tag has no associated topic
		}

		initLabelPrimaryTagLabel(tag.getTitle(), tag.getId());
		propertysetItem.addItemProperty(PRIMARY_TAG_FIELD, new ObjectProperty<>(tag.getId()));
		propertysetItem.addItemProperty(PRIMARY_TAG_TITLE_FIELD, new ObjectProperty<>(tag.getTitle()));

		getPropertyDataSource().setValue(propertysetItem);
	};

	private String getContentUriJCRFieldName() {
		return this.contentUriJCRFieldName;
	}

	private TagService getTagService() {
		return tagService;
	}

	private Label initLabel(String message, String value, Label component) {
		String label = String.format("<b>%s %s</b>", message, value);

		if (component == null) {
			component = new Label(label, ContentMode.HTML);
			rootLayout.addComponent(component);
		} else {
			component.setValue(label);
		}

		return component;
	}


	protected abstract String buildContentUriLabel(String topicUri, String nodeName);


	@Override
	public Class<? extends PropertysetItem> getType() {
		return PropertysetItem.class;
	}
}

