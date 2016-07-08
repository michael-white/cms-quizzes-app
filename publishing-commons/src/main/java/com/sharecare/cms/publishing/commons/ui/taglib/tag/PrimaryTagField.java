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
public class PrimaryTagField extends CustomField<PropertysetItem> {


	public static final String PRIMARY_TAG_FIELD = "primaryTag";
	public static final String PRIMARY_TAG_TITLE_FIELD = "primaryTagTitle";
	public static final String TOPIC_URI_FIELD = "topicUri";

	private static final String SELECTED_URI_LABEL = "Selected Article Uri:";
	private static final String SELECTED_PRIMARY_TAG_LABEL = "Selected Primary Tag:";

	private final TagService     tagService;
	private final JcrNodeAdapter currentItem;

	public PrimaryTagField(TagService tagService, JcrNodeAdapter currentItem) {
		this.tagService = tagService;
		this.currentItem = currentItem;
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
			String articleUri = isNullOrEmpty(savedValues.getItemProperty("articleUriWebPath")); // TODO FIX THIS
			String primaryTag = isNullOrEmpty(savedValues.getItemProperty(PRIMARY_TAG_FIELD));
			String primaryTagTitle = isNullOrEmpty(savedValues.getItemProperty(PRIMARY_TAG_TITLE_FIELD));
			articleUriLabel = initLabel(SELECTED_URI_LABEL, articleUri, articleUriLabel);
			primaryTagLabel = initLabelPrimaryTagLabel(primaryTagTitle, primaryTag);
		}
	}

	private Label initLabelPrimaryTagLabel(String primaryTagTitle, String primaryTag) {
		return initLabel(SELECTED_PRIMARY_TAG_LABEL, primaryTagTitle + " (" + primaryTag + ")", primaryTagLabel);
	}


	private HorizontalLayout initSelectTagComponents() {
		List<TagResult> topLevelTags = tagService.getAllTopLevelTags();
		HorizontalLayout selectMenuLayout = new HorizontalLayout();
		selectMenuLayout.addComponent(new SelectTagDropdown(topLevelTags, onTagSelected, tagService, selectMenuLayout));

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
				String articleUriFullPath = buildArticleUriLabel(topicUri, getCurrentItem().getNodeName());
				initLabel(SELECTED_URI_LABEL, articleUriFullPath, articleUriLabel);
				propertysetItem.addItemProperty(TOPIC_URI_FIELD, new ObjectProperty<>(topicUri));
				propertysetItem.addItemProperty("articleUriWebPath", new ObjectProperty<>(articleUriFullPath)); // TODO FIX THIS
			}
		} catch (ResourceNotFoundException r) {
			// its ok. Tag has no associated topic
		}

		initLabelPrimaryTagLabel(tag.getTitle(), tag.getId());
		propertysetItem.addItemProperty(PRIMARY_TAG_FIELD, new ObjectProperty<>(tag.getId()));
		propertysetItem.addItemProperty(PRIMARY_TAG_TITLE_FIELD, new ObjectProperty<>(tag.getTitle()));

		getPropertyDataSource().setValue(propertysetItem);
	};

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


	private String buildArticleUriLabel(String topicUri, String nodeName) {
		return String.format("/health/%s/article/%s", topicUri, nodeName).toLowerCase();
	}

	@Override
	public Class<? extends PropertysetItem> getType() {
		return PropertysetItem.class;
	}
}

