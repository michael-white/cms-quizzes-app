package com.sharecare.cms.articles.ui.tag.components;


import java.util.ArrayList;
import java.util.List;

import com.sharecare.cms.articles.ui.tag.remote.TagResult;
import com.sharecare.cms.articles.ui.tag.remote.TagService;
import com.vaadin.ui.*;

public class SearchFieldComponent extends CustomComponent {

	private final TagService tagService;

	private SearchResultsTable resultsTable;


	public SearchFieldComponent(SearchResultsTable resultsTable,
								TagService tagService)  {

		this.resultsTable = resultsTable;
		this.tagService = tagService;

		FormLayout fl = new FormLayout();
		fl.setSizeFull();

		fl.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

		TextField tagIdField = new TextField("Tag ID");
		fl.addComponent(tagIdField);

		TextField keywordField = new TextField("Keywords");
		fl.addComponent(keywordField);

		fl.addComponent(new SearchTagButton(tagIdField, keywordField));

		setCompositionRoot(fl);
	}

	private class SearchTagButton extends CustomComponent {

		private final TextField tagIdField;
		private final TextField keywordField;

		public SearchTagButton(TextField tagIdField, TextField keywordField) {
			this.tagIdField = tagIdField;
			this.keywordField = keywordField;

			Button button = new Button("Search", this::search);
			button.addStyleName("magnoliabutton");
			setCompositionRoot(button);
		}

		public void search(Button.ClickEvent event) {
			resultsTable.populateResultsTable(searchTags());
			resultsTable.setVisible(true);
		}

		private List<TagResult> searchTags() {
			List<TagResult> tagResults = new ArrayList<>();

			if (!tagIdField.isEmpty())
				tagResults.add(tagService.loadByTagID(tagIdField.getValue()));

			else if (!keywordField.isEmpty())
				tagResults = tagService.searchByKeywords(keywordField.getValue());

			return tagResults;
		}
	}


}
