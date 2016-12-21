package com.sharecare.cms.guides.configuration;

import com.sharecare.cms.publishing.commons.configuration.RemoteServerResourceConfig;
import lombok.Data;

import java.util.Map;

/**
 * Created by robert.davis on 12/20/2016.
 */
@Data
public class HealthGuideModuleConfig {
    private Map<String, Map<String, RemoteServerResourceConfig>> publishing;
}
