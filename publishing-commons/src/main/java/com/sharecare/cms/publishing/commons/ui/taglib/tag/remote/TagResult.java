package com.sharecare.cms.publishing.commons.ui.taglib.tag.remote;

import lombok.*;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class TagResult implements Serializable, Comparable<TagResult> {

	private String _id;
	private String title;
	private String description;
	private TagReference parent;

	public String getId() {
		return _id;
	}

	@SuppressWarnings("NullableProblems")
	public int compareTo(TagResult tagResult) {
		return title.compareTo(tagResult.title);
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TagReference {
		String id;
		String type;
	}

}
