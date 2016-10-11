package com.sharecare.cms.articles.configuration;

import java.util.Map;

import com.sharecare.cms.publishing.commons.configuration.RemoteServerResourceConfig;
import lombok.Data;

@Data
public class SlideshowsModuleConfig {

    private Map<String, Map<String, RemoteServerResourceConfig>> publishing;

}
