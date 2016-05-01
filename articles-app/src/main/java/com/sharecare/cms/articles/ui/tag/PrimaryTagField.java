package com.sharecare.cms.articles.ui.tag;

import java.util.List;
import java.util.function.BiConsumer;

import com.sharecare.cms.articles.schema.ArticleJCRSchema;
import com.sharecare.cms.articles.ui.tag.components.SearchFieldComponent;
import com.sharecare.cms.articles.ui.tag.components.SearchResultsTable;
import com.sharecare.cms.articles.ui.tag.components.SelectTagDropdown;
import com.sharecare.cms.articles.ui.tag.remote.ResourceNotFoundException;
import com.sharecare.cms.articles.ui.tag.remote.TagResult;
import com.sharecare.cms.articles.ui.tag.remote.TopicResult;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

@Getter
public class PrimaryTagField extends CustomField<PropertysetItem> {


	private static final String SELECTED_URI_LABEL = "Selected Article Uri:";
	private static final String SELECTED_PRIMARY_TAG_LABEL = "Selected Primary Tag:";

	private final TagService tagService;
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
			String articleUri = isNullOrEmpty(savedValues.getItemProperty(ArticleJCRSchema.articleUri.name()));
			String primaryTag = isNullOrEmpty(savedValues.getItemProperty(ArticleJCRSchema.primaryTag.name()));
			String primaryTagTitle = isNullOrEmpty(savedValues.getItemProperty(ArticleJCRSchema.primaryTagTitle.name()));
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
				String articleUri = buildArticleUri(topicUri, getCurrentItem().getNodeName());
				initLabel(SELECTED_URI_LABEL, articleUri, articleUriLabel);
				propertysetItem.addItemProperty(ArticleJCRSchema.topicUri.name(), new ObjectProperty<>(topicUri));
				propertysetItem.addItemProperty(ArticleJCRSchema.articleUri.name(), new ObjectProperty<>(articleUri));
			}
		} catch (ResourceNotFoundException r) {
			// its ok. Tag has no associated topic
		}

		initLabelPrimaryTagLabel(tag.getTitle(), tag.getId());
		propertysetItem.addItemProperty(ArticleJCRSchema.primaryTag.name(), new ObjectProperty<>(tag.getId()));
		propertysetItem.addItemProperty(ArticleJCRSchema.primaryTagTitle.name(), new ObjectProperty<>(tag.getTitle()));

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


	private String buildArticleUri(String topicUri, String nodeName) {
		return String.format("/health/%s/article/%s", topicUri, nodeName).toLowerCase();
	}

	@Override
	public Class<? extends PropertysetItem> getType() {
		return PropertysetItem.class;
	}
}

