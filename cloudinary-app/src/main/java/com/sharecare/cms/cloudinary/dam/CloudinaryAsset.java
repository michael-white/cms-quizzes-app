package com.sharecare.cms.cloudinary.dam;

import java.io.InputStream;
import java.util.Calendar;

import info.magnolia.dam.api.*;
import info.magnolia.dam.api.metadata.AssetMetadata;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class CloudinaryAsset extends AbstractCloudinaryItem implements Asset {

	protected CloudinaryAsset(CloudinaryAssetProvider assetProvider, ItemKey key) {
		super(assetProvider, key);
	}

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
	public Folder getParent() {
		String fullPath = StringUtils.substringBeforeLast(StringUtils.removeEnd(getPath(), "/"), "/") + "/";
		try {
			return getAssetProvider().getFolder(fullPath);
		} catch (PathAwareAssetProvider.PathNotFoundException e) {
			log.warn("Unable to obtain parent for item {}", getPath());
		}
		return null;
	}

	@Override
	public boolean isFolder() {
		return false;
	}

	@Override
	public boolean isAsset() {
		return true;
	}
}
