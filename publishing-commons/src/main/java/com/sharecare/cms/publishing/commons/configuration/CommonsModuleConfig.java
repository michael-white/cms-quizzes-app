package com.sharecare.cms.publishing.commons.configuration;

import lombok.Data;

import java.util.Map;

@Data
public class CommonsModuleConfig {

    public enum Environments {
        release, qa, dev
    }

    private Map<String, String>        webHost;
    private RemoteServerResourceConfig tagResource;
    private String                     environment;


}
