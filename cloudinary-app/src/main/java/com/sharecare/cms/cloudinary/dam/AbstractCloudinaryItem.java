package com.sharecare.cms.cloudinary.dam;

import static com.sharecare.cms.cloudinary.dam.CloudinaryAssetProvider.*;

import info.magnolia.dam.api.AbstractItem;
import info.magnolia.dam.api.ItemKey;
import org.apache.commons.lang.StringUtils;

public abstract class AbstractCloudinaryItem extends AbstractItem<CloudinaryAssetProvider> {

	protected AbstractCloudinaryItem(CloudinaryAssetProvider assetProvider, ItemKey key) {
		super(assetProvider, key);
	}

	@Override
	public String getName() {
		String possibleName = StringUtils.removeEnd(getItemKey().getAssetId(), PATH_SEPARATOR);
		return StringUtils.contains(possibleName, PATH_SEPARATOR) ? StringUtils.substringAfterLast(possibleName, PATH_SEPARATOR) : possibleName;
	}

	@Override
	public String getPath() {
		return getItemKey().getAssetId();
	}

}
