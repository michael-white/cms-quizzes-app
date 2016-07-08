package com.sharecare.cms.publishing.commons.ui.taglib.tag.remote;

import java.util.List;

public interface TagService {

	TagResult loadByTagID(String value);

	List<TagResult> searchByKeywords(String value);

	TopicResult getTopicForTag(String tagId);

	List<TagResult> getAllTopLevelTags();

	List<TagResult> getChildrenForTag(String tagId);
}
