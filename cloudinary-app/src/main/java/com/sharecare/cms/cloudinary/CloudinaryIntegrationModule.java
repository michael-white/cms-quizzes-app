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

import info.magnolia.amazon.s3.dam.AmazonS3ClientService;
import info.magnolia.license.*;
import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;

/**
 * Module class.
 */
public class CloudinaryIntegrationModule implements ModuleLifecycle, EnterpriseLicensedModule {

    private static final String MODULE_NAME = "cloudinary-app-module";

    private String credentialsDialogId;

    private String accessKey;

    private String secretAccessKey;

    private AmazonS3ClientService amazonS3ClientService;

    @Inject
    public CloudinaryIntegrationModule(AmazonS3ClientService amazonS3ClientService) {
        this.amazonS3ClientService = amazonS3ClientService;
    }

    @Override
    public void start(ModuleLifecycleContext moduleLifecycleContext) {
        amazonS3ClientService.init();
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

    public String getCredentialsDialogId() {
        return credentialsDialogId;
    }

    public void setCredentialsDialogId(String credentialsDialogId) {
        this.credentialsDialogId = credentialsDialogId;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }
}
