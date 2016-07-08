package com.sharecare.cms.publishing.commons.ui.taglib.tag.components;

import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagResult;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;

import java.util.List;
import java.util.function.BiConsumer;

import static com.google.common.base.Strings.nullToEmpty;

public class SearchResultsTable extends CustomComponent {

	private final BiConsumer<Component, TagResult> tagSelectedConsumer;

	public SearchResultsTable(BiConsumer<Component, TagResult> tagSelectedConsumer) {
		this.tagSelectedConsumer = tagSelectedConsumer;

		Table table = initTable();
		setBehaviour(table);
		setCompositionRoot(table);
		setVisible(false);
	}

	public void populateResultsTable(List<TagResult> tagResults) {
		Table table = (Table) getCompositionRoot();
		table.removeAllItems();
		tagResults.stream()
				.sorted((v1, v2) -> v1.getTitle().compareTo(v2.getTitle()))
				.forEach(v -> table.addItem(new Object[]{v.getId(), nullToEmpty(v.getTitle()), nullToEmpty(v.getParent() != null ? v.getParent().getId(): "")}, v));
	}

	private Table initTable() {

		Table table = new Table("Search Results");
		table.setSelectable(true);
		table.setImmediate(true);
		table.setNullSelectionAllowed(false);
		table.setMultiSelect(false);
		table.setPageLength(5);

		table.addContainerProperty("id", String.class, null);
		table.addContainerProperty("title", String.class, null);
		table.addContainerProperty("pTag", String.class, null);

		table.setColumnHeader("id", "ID");
		table.setColumnHeader("title", "Title");
		table.setColumnHeader("pTag", "Primary Tag");
		table.setWidth("100%");
		return table;
	}

	private void setBehaviour(Table table) {
		table.addValueChangeListener(e -> tagSelectedConsumer.accept(getCompositionRoot().getParent(),  (TagResult) e.getProperty().getValue()));
	}
}
