package com.sharecare.cms.articles.ui.taglib;

import com.sharecare.cms.publishing.commons.ui.taglib.tag.PrimaryTagField;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.remote.TagService;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;

public class ArticleUriPrimaryTagField extends PrimaryTagField {

    private final String subDomain;

    public ArticleUriPrimaryTagField(TagService tagService, JcrNodeAdapter currentItem, String subDomain, String contentUriJCRFieldName) {
        super(tagService, currentItem, contentUriJCRFieldName);
        this.subDomain = subDomain;
    }

    @Override
    public String buildContentUriLabel(String topicUri, String nodeName) {
        return String.format("/health/%s/%s/%s", topicUri, subDomain ,nodeName).toLowerCase();
    }
}
