package com.sharecare.cms.publishing.commons.ui.taglib.tag.components;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagResult;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagService;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;

@Getter
public class SelectTagDropdown  extends CustomComponent {

	private final BiConsumer<Component, TagResult> tagSelectedConsumer;
	private final TagService                       tagService;


	private SelectTagDropdown child;
	private HorizontalLayout selectMenuLayout;

	public SelectTagDropdown(List<TagResult> topLevelTags,
							 BiConsumer<Component, TagResult> tagSelectedConsumer,
							 TagService tagService,
							 HorizontalLayout selectMenuLayout) {
		this.tagSelectedConsumer = tagSelectedConsumer;
		this.tagService = tagService;
		this.selectMenuLayout = selectMenuLayout;

		BeanItemContainer<TagResult> container = new BeanItemContainer<>(TagResult.class);
		topLevelTags.forEach(container::addItem);

		NativeSelect tagSelect = new NativeSelect("", container);
		tagSelect.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
		tagSelect.setItemCaptionPropertyId("title");
		tagSelect.addValueChangeListener(onSelection::accept);
		setCompositionRoot(tagSelect);

	}

	Consumer<Property.ValueChangeEvent> onSelection = event -> {
		removeChildren(child);

		TagResult selected = (TagResult) event.getProperty().getValue();
		getTagSelectedConsumer().accept(getCompositionRoot().getParent(), selected);
		List<TagResult> children = getTagService().getChildrenForTag(selected.getId());
		if (CollectionUtils.isNotEmpty(children)) {
			child = new SelectTagDropdown(children, getTagSelectedConsumer(), getTagService(), selectMenuLayout);
			selectMenuLayout.addComponent(child);
		}
	};

	private void removeChildren(SelectTagDropdown child) {
		if (child != null) {
			ComponentContainer parent = (ComponentContainer) child.getParent();
			if (parent != null) {
				parent.removeComponent(child);
				removeChildren(child.getChild());
			}
		}
	}
}
