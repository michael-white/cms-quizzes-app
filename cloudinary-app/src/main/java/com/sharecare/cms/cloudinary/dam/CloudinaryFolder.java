package com.sharecare.cms.cloudinary.dam;

import java.util.Iterator;

import info.magnolia.dam.api.AssetProvider;
import info.magnolia.dam.api.Folder;
import info.magnolia.dam.api.Item;
import info.magnolia.dam.api.ItemKey;

public class CloudinaryFolder implements Folder {

	@Override
	public Iterator<Item> getChildren() {
		return null;
	}

	@Override
	public Item getItem(String name) {
		return null;
	}

	@Override
	public boolean isRoot() {
		return false;
	}

	@Override
	public ItemKey getItemKey() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public Folder getParent() {
		return null;
	}

	@Override
	public boolean isFolder() {
		return false;
	}

	@Override
	public boolean isAsset() {
		return false;
	}

	@Override
	public AssetProvider getAssetProvider() {
		return null;
	}
}
