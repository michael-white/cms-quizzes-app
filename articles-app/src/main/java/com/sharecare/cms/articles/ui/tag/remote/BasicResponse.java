package com.sharecare.cms.articles.ui.tag.remote;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BasicResponse<T> {

	T entity;
	int statusCode;
}
