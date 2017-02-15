package com.sharecare.cms;

import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.api.app.AppContext;
import info.magnolia.ui.api.app.AppView;
import info.magnolia.ui.api.app.SubAppDescriptor;
import info.magnolia.ui.api.location.DefaultLocation;
import info.magnolia.ui.api.location.Location;
import info.magnolia.ui.contentapp.ContentApp;
import info.magnolia.ui.contentapp.browser.BrowserSubAppDescriptor;
import info.magnolia.ui.vaadin.integration.contentconnector.JcrContentConnectorDefinition;

import javax.inject.Inject;

public class QuizzesApp extends ContentApp {

    @Inject
    public QuizzesApp(AppContext appContext, AppView view, ComponentProvider componentProvider) {
        super(appContext, view, componentProvider);
    }

    @Override
    public void start(Location location) {
        super.start(location);
        SubAppDescriptor first = null;
        for (SubAppDescriptor subAppDescriptor : appContext.getAppDescriptor().getSubApps().values()) {
            if (first == null) {
                first = subAppDescriptor;
                continue;
            }
            BrowserSubAppDescriptor browserSubAppDescriptor = (BrowserSubAppDescriptor) first;
            JcrContentConnectorDefinition connectorDefinition = ((JcrContentConnectorDefinition)browserSubAppDescriptor.getContentConnector());
            String path = connectorDefinition.getRootPath();
            if(!subAppDescriptor.getName().equalsIgnoreCase("detail")) {
                getAppContext().openSubApp(new DefaultLocation(Location.LOCATION_TYPE_APP, first.getLabel(), subAppDescriptor.getName(), path));
            }
        }
        BrowserSubAppDescriptor browserSubAppDescriptor = (BrowserSubAppDescriptor) first;
        JcrContentConnectorDefinition connectorDefinition = ((JcrContentConnectorDefinition)browserSubAppDescriptor.getContentConnector());
        String path = connectorDefinition.getRootPath();
        getAppContext().openSubApp(new DefaultLocation(Location.LOCATION_TYPE_APP, first.getLabel(), first.getName(), path));
    }

}
