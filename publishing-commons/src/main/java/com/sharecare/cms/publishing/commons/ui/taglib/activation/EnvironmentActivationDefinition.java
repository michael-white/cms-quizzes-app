package com.sharecare.cms.publishing.commons.ui.taglib.activation;

import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;

public class EnvironmentActivationDefinition extends ConfiguredFieldDefinition {

    private String webUriField;

    public String getWebUriField() {
        return webUriField;
    }

    public void setWebUriField(String webUriField) {
        this.webUriField = webUriField;
    }
}
