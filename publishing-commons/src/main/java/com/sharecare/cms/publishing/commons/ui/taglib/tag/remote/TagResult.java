package com.sharecare.cms.publishing.commons.ui.taglib.tag.remote;

import java.io.Serializable;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class TagResult implements Serializable {

	private String _id;
	private String title;
	private String description;
	private TagReference parent;

	public String getId() {
		return _id;
	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TagReference {
		String id;
		String type;
	}

}
