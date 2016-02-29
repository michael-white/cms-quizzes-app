package com.sharecare.cms.articles.ui.tag.remote;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class DataServerResponse<T> {

	private String type;
	private T response;

}
