package com.sharecare.cms.articles.ui.tag.remote;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CollectionResponse<T> {

	List<T> entity;
	int statusCode;
}
