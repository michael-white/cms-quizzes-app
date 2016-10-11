package com.sharecare.cms.slideshows.configuration;

import java.util.Map;

import com.sharecare.cms.publishing.commons.configuration.RemoteServerResourceConfig;
import lombok.Data;

@Data
public class SlideshowsModuleConfig {

    private Map<String, Map<String, RemoteServerResourceConfig>> publishing;

}
