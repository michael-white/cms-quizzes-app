package com.sharecare.cms.cloudinary.dam;

import java.io.InputStream;
import java.util.Calendar;

import info.magnolia.dam.api.Asset;
import info.magnolia.dam.api.AssetProvider;
import info.magnolia.dam.api.Folder;
import info.magnolia.dam.api.ItemKey;
import info.magnolia.dam.api.metadata.AssetMetadata;

public class CloudinaryAsset implements Asset {

	@Override
	public String getLink() {
		return null;
	}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public String getSubject() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public String getCaption() {
		return null;
	}

	@Override
	public String getLanguage() {
		return null;
	}

	@Override
	public String getCopyright() {
		return null;
	}

	@Override
	public String getComment() {
		return null;
	}

	@Override
	public Calendar getCreated() {
		return null;
	}

	@Override
	public Calendar getLastModified() {
		return null;
	}

	@Override
	public <M extends AssetMetadata> boolean supports(Class<M> metaDataType) {
		return false;
	}

	@Override
	public <M extends AssetMetadata> M getMetadata(Class<M> metaDataType) {
		return null;
	}

	@Override
	public String getMimeType() {
		return null;
	}

	@Override
	public long getFileSize() {
		return 0;
	}

	@Override
	public String getFileName() {
		return null;
	}

	@Override
	public InputStream getContentStream() {
		return null;
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
