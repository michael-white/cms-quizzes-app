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
package com.sharecare.cms.cloudinary.ui;

import javax.inject.Inject;
import javax.inject.Provider;

import com.amazonaws.AmazonServiceException;
import info.magnolia.amazon.s3.AmazonS3IntegrationModule;
import info.magnolia.amazon.s3.dam.AmazonS3ClientService;
import info.magnolia.amazon.s3.ui.AmazonS3App;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.app.AppController;
import info.magnolia.ui.api.app.AppView;
import info.magnolia.ui.api.location.Location;
import info.magnolia.ui.api.overlay.AlertCallback;
import info.magnolia.ui.api.shell.Shell;
import info.magnolia.ui.contentapp.ContentApp;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom app impl that opens credentials dialog when Amazon S3 credentials are not configured or are wrong.
 */
public class CloudinaryApp extends ContentApp {

    private static final Logger log = LoggerFactory.getLogger(AmazonS3App.class);

    private final Provider<AmazonS3IntegrationModule> provider;
    private final SimpleTranslator i18n;
    private final AppController appController;
    private Shell shell;
    private final AmazonS3ClientService amazonS3ClientService;

    @Inject
    public CloudinaryApp(AppContext appContext, AppView view, ComponentProvider componentProvider, Provider<AmazonS3IntegrationModule> provider, SimpleTranslator i18n, AppController appController, Shell shell, AmazonS3ClientService amazonS3ClientService) {
        super(appContext, view, componentProvider);
        this.provider = provider;
        this.i18n = i18n;
        this.appController = appController;
        this.shell = shell;
        this.amazonS3ClientService = amazonS3ClientService;
    }

    @Override
    public void start(final Location location) {
        AmazonS3IntegrationModule module = provider.get();
        if (!module.isLicenseValid()) {
            getAppContext().openAlert(MessageStyleTypeEnum.ERROR,
                    i18n.translate("amazon-s3-connector-module.licenseError.title"),
                    i18n.translate("amazon-s3-connector-module.licenseError.body", StringUtils.join(module.getSupportedEditions(), ", ")),
                    i18n.translate("amazon-s3-connector-module.licenseError.confirm"),
                    new AlertCallback() {
                        @Override
                        public void onOk() {
                        }
                    });
            return;
        }

        String accessKey = module.getAccessKey();
        String secretAccessKey = module.getSecretAccessKey();

        // we want to show error popup if access key or secret key is empty
        boolean showError = false;
        if (StringUtils.isBlank(accessKey) || StringUtils.isBlank(secretAccessKey)) {
            showError = true;
        } else {
            // someone might have filled both keys via config app, we have to check if passed credentials are valid
            // so issue a simple call to S3 service to check that
            try {
                amazonS3ClientService.createDefaultClient().getS3AccountOwner();
            } catch (AmazonServiceException e) {
                if (e.getErrorCode().equals("InvalidAccessKeyId")) {
                    showError = true;
                    log.debug("Invalid credentials (access key and secret access key, please check your configuration.", e);
                } else {
                    shell.openNotification(MessageStyleTypeEnum.ERROR, true, i18n.translate("amazon-s3-connector-module.credentials.validation.error", e.getErrorMessage()));
                    log.debug("Unable to open S3 App", e);
                    return;
                }
            }
        }
        if (showError) {
            shell.openAlert(MessageStyleTypeEnum.ERROR,
                    i18n.translate("amazon-s3-connector-module.keyError.title"),
                    i18n.translate("amazon-s3-connector-module.keyError.body"),
                    i18n.translate("amazon-s3-connector-module.keyError.confirm"), new AlertCallback() {
                        @Override
                        public void onOk() {
                            appController.stopApp(getAppContext().getName());
                        }
                    });
        } else {
            super.start(location);
        }
    }
}
