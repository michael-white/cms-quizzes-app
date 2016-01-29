package com.sharecare.cms.publishing.commons.bootstrap;

import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.ModuleBootstrapTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContentModuleBootstrapTask extends ModuleBootstrapTask {

	@Override
	protected boolean acceptResource(InstallContext ctx, String resourceName){
		boolean acceptResource = super.acceptResource(ctx, resourceName);
		try{
			if(acceptResource){
				String nodePath = resourceName.substring(resourceName.lastIndexOf("/") + 1, resourceName.indexOf(".xml"));
				String workspace = nodePath.substring(0, nodePath.indexOf("."));
				nodePath = nodePath.substring(nodePath.indexOf(".")).replace(".", "/");
				acceptResource = !ctx.getHierarchyManager(workspace).isExist(nodePath);
				if(acceptResource){
					log.debug("Bootstrapping missing node: " + resourceName);
				}
			}
		} catch (Exception e){
			String errorMsg = "Unable to determine whether the resource is legit.\n" + e.getMessage();
			log.error(errorMsg);
			if(log.isInfoEnabled()) {
				log.info(errorMsg, e);
			}
		}

		return acceptResource;
	}

}
