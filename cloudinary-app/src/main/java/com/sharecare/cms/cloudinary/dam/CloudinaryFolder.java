package com.sharecare.cms.cloudinary.dam;

import static com.sharecare.cms.cloudinary.dam.CloudinaryAssetProvider.*;

import java.util.Iterator;

import info.magnolia.dam.api.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;


/**
 * Class representing a folder in the Cloudinary Cloud storage.<br/>
 * Note that folders are in fact "emulated" as zero-sized assets with '/' at the end of their name because there is no folder support in  Cloudinary.
 */
@Slf4j
public class CloudinaryFolder extends AbstractCloudinaryItem implements  Folder {

	protected CloudinaryFolder(CloudinaryAssetProvider assetProvider, ItemKey key) {
		super(assetProvider, key);
	}

	@Override
	public Iterator<Item> getChildren() {
		return getAssetProvider().getChildren(this);
	}

	@Override
	public Item getItem(String name) {
		String fullPath = getPath() + name;
		try {
			return getAssetProvider().getItem(fullPath);
		} catch (PathAwareAssetProvider.PathNotFoundException e) {
			log.warn("Unable to obtain item {}", fullPath);
		}
		return null;
	}

	@Override
	public boolean isRoot() {
		return getAssetProvider().getRootFolder().getItemKey().equals(getItemKey());
	}

	@Override
	public Folder getParent() {
		if (isRoot()) {
			return null;
		}
		String fullPath = StringUtils.substringBeforeLast(StringUtils.removeEnd(getPath(), PATH_SEPARATOR), PATH_SEPARATOR) + PATH_SEPARATOR;
		try {
			return getAssetProvider().getFolder(fullPath);
		} catch (PathAwareAssetProvider.PathNotFoundException e) {
			log.warn("Unable to obtain parent for item {}", fullPath);
		}
		return null;
	}

	@Override
	public boolean isFolder() {
		return true;
	}

	@Override
	public boolean isAsset() {
		return false;
	}

}
