package com.sharecare.cms.slideshows.ui.taglib;

import com.sharecare.cms.publishing.commons.ui.taglib.tag.PrimaryTagField;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagService;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

public class ArticleUriPrimaryTagField extends PrimaryTagField {

    public ArticleUriPrimaryTagField(TagService tagService, JcrNodeAdapter currentItem, String contentUriJCRFieldName) {
        super(tagService, currentItem, contentUriJCRFieldName);
    }

    @Override
    public String buildContentUriLabel(String topicUri, String nodeName) {
        return String.format("/health/%s/article/%s", topicUri, nodeName).toLowerCase();
    }
}
