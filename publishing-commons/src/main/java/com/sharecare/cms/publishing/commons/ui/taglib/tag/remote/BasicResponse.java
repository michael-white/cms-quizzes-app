package com.sharecare.cms.publishing.commons.ui.taglib.tag.remote;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BasicResponse<T> {

	T entity;
	int statusCode;
}
