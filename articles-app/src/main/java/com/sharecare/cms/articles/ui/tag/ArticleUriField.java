package com.sharecare.cms.articles.ui.tag;

import java.util.List;
import java.util.function.BiConsumer;

import com.sharecare.cms.articles.ui.tag.components.SearchFieldComponent;
import com.sharecare.cms.articles.ui.tag.components.SearchResultsTable;
import com.sharecare.cms.articles.ui.tag.components.SelectTagDropdown;
import com.sharecare.cms.articles.ui.tag.remote.ResourceNotFoundException;
import com.sharecare.cms.articles.ui.tag.remote.TagResult;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.apache.commons.lang.StringUtils;

public class ArticleUriField extends CustomField<PropertysetItem> {


	public static final String TOPIC_URI = "topicUri";
	public static final String PRIMARY_TAG = "primaryTag";
	public static final String SELECTED_URI_LABEL = "Selected Uri:";
	public static final String SELECTED_PRIMARY_TAG_LABEL = "Selected Primary Tag:";

	private final TagService tagService;

	public ArticleUriField(TagService tagService) {
		this.tagService = tagService;
	}


	private VerticalLayout rootLayout;

	private Label articleUriLabel;
	private Label primaryTagLabel;

	@Override
	protected Component initContent() {

		rootLayout = new VerticalLayout();
		rootLayout.setSizeFull();
		rootLayout.setSpacing(true);

		SearchResultsTable resultsTable = new SearchResultsTable(onTagSelected);
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
			String articleUri = isNullOrEmpty(savedValues.getItemProperty(TOPIC_URI));
			String primaryTag = isNullOrEmpty(savedValues.getItemProperty(PRIMARY_TAG));
			articleUriLabel = initLabel(SELECTED_URI_LABEL, articleUri, articleUriLabel);
			primaryTagLabel = initLabel(SELECTED_PRIMARY_TAG_LABEL, primaryTag, primaryTagLabel);
		}
	}

	private HorizontalLayout initSelectTagComponents() {
		List<TagResult> topLevelTags = tagService.getAllTopLevelTags();
		HorizontalLayout selectMenuLayout = new HorizontalLayout();
		selectMenuLayout.addComponent(new SelectTagDropdown(topLevelTags, onTagSelected, tagService, selectMenuLayout));

		return selectMenuLayout;
	}

	private String isNullOrEmpty(Property itemProperty) {
		return itemProperty != null ? itemProperty.getValue().toString() : StringUtils.EMPTY;
	}


	private BiConsumer<Component, TagResult> onTagSelected = (parent,tag) -> {
		PropertysetItem propertysetItem = new PropertysetItem();

		try {
			String newArticleUri = getTagService().getTopicForTag(tag.getId()).getUri();
			initLabel(SELECTED_URI_LABEL, newArticleUri, articleUriLabel);
			propertysetItem.addItemProperty(TOPIC_URI, new ObjectProperty<>(newArticleUri));
		} catch (ResourceNotFoundException r) {
			// its ok. Tag has no associated topic
		}

		initLabel(SELECTED_PRIMARY_TAG_LABEL, tag.getId(), primaryTagLabel);
		propertysetItem.addItemProperty(PRIMARY_TAG, new ObjectProperty<>(tag.getId()));

		getPropertyDataSource().setValue(propertysetItem);
	};

	public TagService getTagService() {
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

	@Override
	public Class<? extends PropertysetItem> getType() {
		return PropertysetItem.class;
	}
}

