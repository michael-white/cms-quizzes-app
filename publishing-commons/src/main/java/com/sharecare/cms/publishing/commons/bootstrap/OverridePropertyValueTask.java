package com.sharecare.cms.publishing.commons.bootstrap;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.util.Collection;

import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.CheckAndModifyPropertyValueTask;

public class OverridePropertyValueTask extends CheckAndModifyPropertyValueTask {

    public OverridePropertyValueTask(String name, String description, String workspaceName, String nodePath, String propertyName, String expectedCurrentValue, String newValue) {
        super(name, description, workspaceName, nodePath, propertyName, expectedCurrentValue, newValue);
    }

    /**
     * Checks that the given String property has the expected value. Changes it if so, logs otherwise.
     */
    @Override
    protected void checkAndModifyPropertyValue(InstallContext ctx, Node node, String propertyName, Collection<String> expectedCurrentValues, String newValue) throws RepositoryException {
        final Property prop = node.getProperty(propertyName);

        if (prop == null || !prop.getValue().getString().equals(newValue)) {
            final String msg = format("Property \"{0}\" was expected to exist at {1} with value \"{2}\" but {3,choice,0#does not exist|1#has the value \"{4}\" instead}.",
                    propertyName,
                    node.getPath(),
                    expectedCurrentValues,
                    prop == null ? 1 : 0,
                    prop != null ? prop.getValue().getString() : null);
            ctx.warn(msg);

            if (prop != null) {
                prop.setValue(newValue);
            }
        }
    }
}
