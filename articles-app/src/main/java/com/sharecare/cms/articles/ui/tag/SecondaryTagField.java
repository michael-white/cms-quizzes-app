package com.sharecare.cms.articles.ui.tag;

import java.util.*;
import java.util.function.BiConsumer;

import com.google.common.collect.Lists;
import com.sharecare.cms.articles.ui.tag.components.SelectTagDropdown;
import com.sharecare.cms.articles.ui.tag.remote.TagResult;
import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;

public class SecondaryTagField  extends CustomField<PropertysetItem> {

	public static final String SECONDARY_TAG = "secondaryTag";
	private final TagService tagService;

	private VerticalLayout savedTagsLayout;

	Map<String, String> tagTrain = new HashMap<>();

	public SecondaryTagField(TagService tagService) {
		this.tagService = tagService;
	}

	@Override
	protected Component initContent() {

		VerticalLayout rootLayout = new VerticalLayout();
		rootLayout.setSizeFull();
		rootLayout.setSpacing(true);

//		SearchResultsTable resultsTable = new SearchResultsTable(onTagSelected);
//		SearchFieldComponent searchFieldComponent = new SearchFieldComponent(resultsTable, tagService);
//		rootLayout.addComponent(searchFieldComponent);
//		rootLayout.addComponent(resultsTable);

		List<TagResult> topLevelTags = tagService.getAllTopLevelTags();
		VerticalLayout selectMenuLayout = new VerticalLayout();

		selectMenuLayout.addComponent(initSelectTagComponents(topLevelTags));
		rootLayout.addComponent(selectMenuLayout);

		Button addMoreButton = new Button("Add More");
		addMoreButton.addStyleName(Reindeer.BUTTON_LINK);
		addMoreButton.addClickListener(e -> selectMenuLayout.addComponent(initSelectTagComponents(topLevelTags)));
		rootLayout.addComponent(addMoreButton);

		VerticalLayout selectedTags = new VerticalLayout();
		selectedTags.addComponent(new Label("<b>Selected Secondary tags:</b>", ContentMode.HTML));
		savedTagsLayout = initSavedLabels();
		selectedTags.addComponent(savedTagsLayout);

		rootLayout.addComponent(selectedTags);

		return rootLayout;
	}

	private VerticalLayout initSavedLabels() {
		VerticalLayout verticalLayout = new VerticalLayout();
		PropertysetItem savedValues = getValue();
		if (savedValues != null) {
			List<String> tags =  isNullOrEmpty(savedValues.getItemProperty(SECONDARY_TAG));
			if (!tags.isEmpty()) {
				tags.forEach(tag -> verticalLayout.addComponent(new Label(tag)));
			}
		}

		return verticalLayout;
	}

	private List<String> isNullOrEmpty(Property itemProperty) {
		return itemProperty != null ? (List)itemProperty.getValue() : Lists.newArrayList();
	}

	private HorizontalLayout initSelectTagComponents(List<TagResult> topLevelTags) {
		HorizontalLayout menuLayout = new HorizontalLayout();
		menuLayout.setId(UUID.randomUUID().toString());
		menuLayout.addComponent(new SelectTagDropdown(topLevelTags, onTagSelected, tagService, menuLayout));
		return menuLayout;
	}

	private BiConsumer<Component, TagResult> onTagSelected = (component ,tag) -> {
		tagTrain.put(component.getParent().getId(), tag.getId());

		savedTagsLayout.removeAllComponents();
		tagTrain.values().forEach(v -> savedTagsLayout.addComponent(new Label(v)));

		PropertysetItem propertysetItem = new PropertysetItem();

		propertysetItem.addItemProperty(SECONDARY_TAG, new ObjectProperty<>(tagTrain.values()));

		getPropertyDataSource().setValue(propertysetItem);
	};

	@Override
	public Class<? extends PropertysetItem> getType() {
		return PropertysetItem.class;
	}

}
