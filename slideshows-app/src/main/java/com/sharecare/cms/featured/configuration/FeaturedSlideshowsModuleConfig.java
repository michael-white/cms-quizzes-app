package com.sharecare.cms.featured.configuration;

import com.sharecare.cms.publishing.commons.configuration.RemoteServerResourceConfig;
import lombok.Data;

import java.util.Map;

@Data
public class FeaturedSlideshowsModuleConfig {

    private Map<String, Map<String, RemoteServerResourceConfig>> publishing;

}
