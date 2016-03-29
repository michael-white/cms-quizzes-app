package com.sharecare.cms.publishing.commons.bootstrap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.Delta;
import info.magnolia.module.delta.ModuleFilesExtraction;
import info.magnolia.module.delta.Task;
import info.magnolia.module.model.Version;

public class DefaultVersionHandler extends DefaultModuleVersionHandler {

	@Override
	protected List<Task> getDefaultUpdateTasks(Version forVersion) {
		log.info("Running GlobalVersionHandler.getDefaultUpdateTasks()");
		List<Task> tasks = super.getDefaultUpdateTasks(forVersion);
		tasks.add(new ContentModuleBootstrapTask());
		return tasks;
	}

	@Override
	public List<Delta> getDeltas(InstallContext installContext, Version from) {
		log.info("Running GlobalVersionHandler.getDeltas()");
		if (from == null) {
			return Collections.singletonList(getInstall(installContext));
		}
		List<Delta> deltas = super.getDeltas(installContext, from);
		Version currentVersion = installContext.getCurrentModuleDefinition().getVersion();
		if ( currentVersion.isEquivalent(from) && "SNAPSHOT".equalsIgnoreCase(currentVersion.getClassifier())){
			deltas.add(getDefaultUpdate(installContext));
		}
		return deltas;
	}


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
		log.info("Running GlobalVersionHandler.getStartupTasks()");
		List<Task> tasks = new ArrayList<Task>();
		//Because our ops team removes the JCR on a redeploy
		tasks.add(new ContentModuleBootstrapTask());
		if ("SNAPSHOT".equalsIgnoreCase(installContext
				.getCurrentModuleDefinition().getVersion().getClassifier())) {
			tasks.add(new ModuleFilesExtraction());
			log.warn("Starting SNAPSHOT release; forcing reload of module files.");
		}
		return tasks;
	}
}
