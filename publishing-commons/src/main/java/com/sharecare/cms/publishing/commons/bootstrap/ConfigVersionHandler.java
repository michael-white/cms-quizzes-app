package com.sharecare.cms.publishing.commons.bootstrap;

import java.util.List;

import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Task;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigVersionHandler extends GlobalVersionHandler {

	private static final String OWNER = "dandonov@sharecare.com";

	private static final String LICENSE = "LS0tLS1CRUdJTiBQR1AgTUVTU0FHRS0tLS0tClZlcnNpb246IEJDUEcgdjE\n" +
			"uNDYKCm93SjRuSnZB\n" +
			"eTh6RXhEanY2Zk9OL3l5YUV4bFA3MHppemszUHk4\n" +
			"bkpURTdOSzA0TjZ3M2VyaHllbXFMZ2xaaW4K\n" +
			"WUdDbVlHaGhaV0pwWldLc\n" +
			"zRPd2FvbUJrWUdqR1ZaWmFWSnlabjFkc0cyMnFZeGJObFYrZWwxcGtt\n" +
			"NU\n" +
			"tZbDVLZgpsMS9tVUp5UldKU2FETVI2eWZtNVhLa1ZCWmxGaVNWQTViWWd2Y\n" +
			"m9HUnJvR3BseTUr\n" +
			"U21sT2FtMnFYa2xxVVVGClJabkZxVnlwS1prbFlETV\n" +
			"JZam9JcG01QlVYNWFhakhJMXNRY3JwVFUz\n" +
			"SHpia3FMU1ZLNk9PU3hNakV\n" +
			"3TWJLeE0KSUZjemNIRUt3SHoxc1l6NWYzaXVxaC9MQlpkTE04OU1a\n" +
			"dFI5\n" +
			"ek1pWE15M09LY2hqUTF5bnFtMmJsYTM1bE5lSgptMHdtdlhBTFBlSndzSjZ\n" +
			"oa3JQRjdaVC9X\n" +
			"K1B2MTZXU2JwOWwzZXJidTFIbC9wN2xWVHBySmFYNHRG\n" +
			"M2lGdG9zCk8yN3pmSEgyU25ZVzJjL0h0\n" +
			"Q2FtL2JyUjJlclNlRmRiL25aW\n" +
			"HNxZFFjUGR2N2owVjMrTzM3VFc3bitkbTlXcWUKanQ5dUFLK0tq\n" +
			"ZVk9Cj\n" +
			"1ocUJ3Ci0tLS0tRU5EIFBHUCBNRVNTQUdFLS0tLS0K";

	/**
	 * Forces extraction of the module's files, like template JSPs, from
	 * <code>classes/mgnl-files</code> into the web application's root. This
	 * does not yield a "Magnolia needs to be updated" screen. To avoid
	 * destroying changes, Magnolia will not override files which have been
	 * modified in the web application.
	 * <p>
	 * (In Magnolia, see also Tools, Development tools, to reload at request.)
	 */
	@Override
	protected List<Task> getStartupTasks(InstallContext installContext) {
		log.info("Running ConfigVersionHandler.getStartupTasks()");
		List<Task> tasks = super.getStartupTasks(installContext);

			log.info("Running ConfigVersionHandler - updating license key.");
			//Load license key for both auth and pub
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

		return tasks;
	}
}
