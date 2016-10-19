package com.sharecare.cms.publishing.commons.ui.taglib.tag;

import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrimaryTagFieldDefinition extends ConfiguredFieldDefinition {

    private String webUriField;
    private String subdomain;

}
