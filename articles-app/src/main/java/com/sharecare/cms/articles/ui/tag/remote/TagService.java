package com.sharecare.cms.articles.ui.tag.remote;

import java.util.List;

import com.sharecare.cms.articles.ui.tag.remote.TagResult;
import com.sharecare.cms.articles.ui.tag.remote.TopicResult;

public interface TagService {

	TagResult loadByTagID(String value);

	List<TagResult> searchByKeywords(String value);

	TopicResult getTopicForTag(String tagId);

	List<TagResult> getAllTopLevelTags();

	List<TagResult> getChildrenForTag(String tagId);
}
