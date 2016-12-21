package com.sharecare.cms.publishing.commons.bootstrap;

import java.util.List;

import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.NodeExistsDelegateTask;
import info.magnolia.module.delta.RemoveNodeTask;
import info.magnolia.module.delta.Task;
import info.magnolia.module.model.Version;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigVersionHandler extends GlobalVersionHandler {

    private static final String OWNER = "dan@sharecare.com";

    private static final String LICENSE = "LS0tLS1CRUdJTiBQR1AgTUVTU0FHRS0tLS0tClZlcnNpb246IEJDUEcgdjEuNDYKCm93SjRuSnZBeTh6RXhMaDQ4eU9HcFluL3RSbFB6MDdpemszUHk4bkpURTdOSzA0TisxdXhWRGtrbzFUQk43RkkKd2RoUXdkRFV5c2pReXNCU3dkVWxSTUhJd05DTXF5eTFxRGd6UDYvWU50cFV6MERQUU1jTVJFWno1WmZucFJiWgpwaVRtT1JSbkpCYWxKZ094WG5KK0xsZHFSVUZtVVdJSlVJY3RVTHU1cm9HeHJwRUJWMjUrU21sT2FyRnRhbDVKCmFsRkJVV1p4S2xkcVNtWUoyRndrc1k0NUxFeU1UQXhzckV3Z2R6RndjUXJBM08xNGlQbS82K2VXN3FveXhYTjkKSFhteXpKdGt4TDJpM3pOejZzOVdWcHpFOGI1TzczSnNvN3pKWXViVEYvajlwODErOU9yelViKzR5V3FUSjd1KwpmYVBETzMvWFRua3JMNlhhcFh0ckZodzMzbEt3VGUzOWZ5Rm5WNUZUSlcrRDl6WWUxMXZNcGRqZ0dYMjZ0K2w1Cit3bi9NbzdOTDcwL3ZkbHp1ZFJuOXdxanE1SXpWcWFGM1RiZTR4Z0RBSmhoZ2ZzPQo9RXRQagotLS0tLUVORCBQR1AgTUVTU0FHRS0tLS0tCg==";

    /**
     * Forces extraction of the module's files, like template JSPs, from
     * <code>classes/mgnl-files</code> into the web application's root. This
     * does not yield a "Magnolia needs to be updated" screen. To avoid
     * destroying changes, Magnolia will not override files which have been
     * modified in the web application.
     * (In Magnolia, see also Tools, Development tools, to reload at request.)
     */
    @Override
    protected List<Task> getStartupTasks(InstallContext installContext) {
        log.info("Running ConfigVersionHandler.getStartupTasks()");
        List<Task> tasks = super.getStartupTasks(installContext);

        log.info("Running ConfigVersionHandler - updating license key.");

        tasks.add(new OverridePropertyValueTask(
                "License",
                "Sets up enterprise",
                "config",
                "/modules/enterprise/license",
                "key",
                "",
                LICENSE));

        tasks.add(new OverridePropertyValueTask(
                "License",
                "Sets up enterprise",
                "config",
                "/modules/enterprise/license",
                "owner",
                "",
                OWNER));

        tasks.add(new NodeExistsDelegateTask("ExistingPubInstances",
                "Checking for existing pub subscribers",
                "config",
                "/server/activation/subscribers",
                new RemoveNodeTask("Removing existing pub subscribers",
                        "Activation does not need pub instances",
                        "config",
                        "/server/activation/subscribers")));

        tasks.add(removeSlideshowsEditDialog);
        return tasks;
    }

    @Override
    protected List<Task> getExtraInstallTasks(InstallContext ctx) {

        log.info("Running ConfigVersionHandler.getExtraInstallTasks()");
        return super.getExtraInstallTasks(ctx);
    }


    private static NodeExistsDelegateTask removeSlideshowsEditDialog = new NodeExistsDelegateTask("Removing the slideshows dialogs config",
            "We clean up and rebuild this using the bootstrapped JCR",
            "config",
            "/modules/app-slideshows/dialogs",
            new RemoveNodeTask("removing cache module",
                    "We clean up and rebuild this using the bootstrapped JCR",
                    "config",
                    "/modules/app-slideshows/dialogs"));

}
