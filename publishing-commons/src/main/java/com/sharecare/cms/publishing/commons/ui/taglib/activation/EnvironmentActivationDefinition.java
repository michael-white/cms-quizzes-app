package com.sharecare.cms.publishing.commons.ui.taglib.activation;

import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnvironmentActivationDefinition extends ConfiguredFieldDefinition {

    private String webUriPattern;

}
