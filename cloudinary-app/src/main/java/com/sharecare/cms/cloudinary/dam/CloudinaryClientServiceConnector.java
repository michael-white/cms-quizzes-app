package com.sharecare.cms.cloudinary.dam;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sharecare.cms.cloudinary.CloudinaryIntegrationModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
@Singleton
public class CloudinaryClientServiceConnector {

    private Provider<CloudinaryIntegrationModule> provider;

    private String apiKey;
    private String secretAccessKey;
    private String cloudName;

    private Cloudinary client;

    @Inject
    public CloudinaryClientServiceConnector(Provider<CloudinaryIntegrationModule> provider) {
        this.provider = provider;
    }

    public synchronized void init() {

        CloudinaryIntegrationModule module = provider.get();
        apiKey = module.getApiKey();
        secretAccessKey = module.getSecretAccessKey();
        cloudName = module.getCloudName();

        if (StringUtils.isNotBlank(apiKey) && StringUtils.isNotBlank(secretAccessKey) && StringUtils.isNotBlank(cloudName)) {
             client = createDefaultClient();
        }
    }

    public Cloudinary createDefaultClient() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", secretAccessKey));
    }

    public Cloudinary getClient() {
        return client;
    }
}
