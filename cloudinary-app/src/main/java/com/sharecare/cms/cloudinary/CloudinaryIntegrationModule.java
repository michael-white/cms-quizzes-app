/**
 * This file Copyright (c) 2015 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This program and the accompanying materials are made
 * available under the terms of the Magnolia Network Agreement
 * which accompanies this distribution, and is available at
 * http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package com.sharecare.cms.cloudinary;

import javax.inject.Inject;

import com.sharecare.cms.cloudinary.dam.CloudinaryClientServiceConnector;
import info.magnolia.amazon.s3.dam.AmazonS3ClientService;
import info.magnolia.license.*;
import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;
import lombok.Getter;
import lombok.Setter;

/**
 * Module class.
 */
@Getter
@Setter
public class CloudinaryIntegrationModule implements ModuleLifecycle, EnterpriseLicensedModule {

    private static final String MODULE_NAME = "cloudinary-app-module";

    private String credentialsDialogId;

    private String apiKey;
    private String secretAccessKey;
    private String cloudName;

    private CloudinaryClientServiceConnector cloudinaryClientServiceConnector;

    @Inject
    public CloudinaryIntegrationModule(CloudinaryClientServiceConnector cloudinaryClientServiceConnector) {
        this.cloudinaryClientServiceConnector = cloudinaryClientServiceConnector;
    }

    @Override
    public void start(ModuleLifecycleContext moduleLifecycleContext) {
        cloudinaryClientServiceConnector.init();
    }

    @Override
    public void stop(ModuleLifecycleContext moduleLifecycleContext) {
    }

    @Override
    public String[] getSupportedEditions() {
        return new String[]{LicenseConsts.EDITION_ENTERPRISE_PRO};
    }

    @Override
    public boolean isDemoAllowed() {
        return true;
    }

    @Override
    public boolean isForceCheck() {
        return false;
    }

    @Override
    public LicenseStatus checkLicense(License license) {
        if (this.isLicenseValid()) {
            return new LicenseStatus(LicenseStatus.STATUS_VALID, "", license);
        } else {
            return new LicenseStatus(LicenseStatus.STATUS_INVALID, "", license);
        }
    }

    public final boolean isLicenseValid() {
        return LicenseManager.getInstance().isLicenseValid(MODULE_NAME);
    }

}
