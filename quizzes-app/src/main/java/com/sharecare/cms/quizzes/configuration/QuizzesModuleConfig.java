package com.sharecare.cms.quizzes.configuration;

import com.sharecare.cms.publishing.commons.configuration.RemoteServerResourceConfig;
import lombok.Data;

import java.util.Map;

@Data
public class QuizzesModuleConfig {
    private Map<String, Map<String, RemoteServerResourceConfig>> publishing;
}
