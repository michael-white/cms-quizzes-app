package com.sharecare.cms.cloudinary.dam;

import static org.apache.commons.lang3.StringUtils.*;
import static org.yaml.snakeyaml.util.UriEncoder.*;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.net.MediaType;
import com.sharecare.cms.cloudinary.util.CloudinaryUtils;
import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.dam.api.*;
import info.magnolia.dam.api.metadata.AssetMetadata;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.framework.message.MessagesManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;

@Slf4j
public class CloudinaryAssetProvider extends AbstractAssetProvider implements PathAwareAssetProvider {

	public static final String PATH_SEPARATOR = "/";

	public static final String ASSET_PROVIDER_ID = "cl:";

	/**
	 * Fake root folder
	 */
	private Folder rootFolder;

	private final Provider<CloudinaryClientServiceConnector> service;

	private MessagesManager messagesManager;

	private SimpleTranslator i18n;

	private ServerConfiguration serverConfiguration;

	private Cache<String, CloudinaryCacheEntry> cache = CacheBuilder.newBuilder().expireAfterAccess(15, TimeUnit.MINUTES).build();
	private Cloudinary client;

	@Inject
	public CloudinaryAssetProvider(Provider<CloudinaryClientServiceConnector> connector, MessagesManager messagesManager, SimpleTranslator i18n, ServerConfiguration serverConfiguration) {
		this.service = connector;
		this.messagesManager = messagesManager;
		this.i18n = i18n;
		this.serverConfiguration = serverConfiguration;
	}

	@Override
	protected EnumSet<AssetProviderCapability> setupProviderCapabilities() {
		return EnumSet.of(AssetProviderCapability.hierarchical);
	}

	@Override
	public boolean supports(Class<? extends AssetMetadata> aClass) {
		return false;
	}

	@Override
	public Item getItem(ItemKey itemKey) throws AssetNotFoundException, IllegalItemKeyException {
		if (getRootFolder().getItemKey().equals(itemKey)) {
			return getRootFolder();
		}
		if (endsWith(itemKey.getAssetId(), PATH_SEPARATOR)) {
			return getFolder(itemKey);
		} else {
			return getAsset(itemKey);
		}
	}

	@Override
	public Asset getAsset(ItemKey itemKey) throws AssetNotFoundException, IllegalItemKeyException {
		Asset asset = getAsset(itemKey.getAssetId());
		if (asset == null) {
			throw new AssetNotFoundException(itemKey);
		}
		return asset;
	}

	@Override
	public Folder getFolder(ItemKey itemKey) throws AssetNotFoundException, IllegalItemKeyException {
		if (getRootFolder().getItemKey().equals(itemKey)) {
			return getRootFolder();
		}
		Folder folder = getFolder(itemKey.getAssetId());
		if (folder == null) {
			throw new AssetNotFoundException(itemKey);
		}
		return folder;
	}

	@Override
	public Item getItem(String path) throws PathNotFoundException {
		if (getRootFolder().getPath().equals(path)) {
			return getRootFolder();
		}
		if (endsWith(path, PATH_SEPARATOR)) {
			return getFolder(path);
		} else {
			return getAsset(path);
		}
	}

	@Override
	public Asset getAsset(String assetPath) throws PathNotFoundException {
		String bucket = CloudinaryUtils.getBucketNameFromPath(assetPath);
		String key = CloudinaryUtils.getObjectKeyFromPath(assetPath);
		Asset asset = getAsset(bucket, key);
		if (asset == null) {
			throw new PathNotFoundException(assetPath);
		}
		return asset;
	}

	@Override
	public Folder getFolder(String folderPath) throws PathNotFoundException {
		if (getRootFolder().getPath().equals(folderPath)) {
			return getRootFolder();
		}

		try {
			String bucket = CloudinaryUtils.getBucketNameFromPath(folderPath);
			String key = CloudinaryUtils.getObjectKeyFromPath(folderPath);
			return new CloudinaryFolder(this, createItemKeyForItem(bucket, key));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public Folder getRootFolder() {
		if (rootFolder == null) {
			try {
				rootFolder = new CloudinaryFolder(this, ItemKey.from(ASSET_PROVIDER_ID + PATH_SEPARATOR));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rootFolder;
	}

	@Override
	public Iterator<Item> list(AssetQuery assetQuery) {
		return IteratorUtils.emptyIterator();
	}

	@Override
	public AssetRenderer getRendererFor(Asset asset, MediaType mediaType) {
		return null;
	}

	public void refresh() {
	}


	private Asset getAsset(String bucket, String key) {
		return new CloudinaryAsset(this,createItemKeyForItem(bucket, key));
	}

	private ItemKey createItemKeyForItem(String bucketName, String key) {
		if (isBlank(key)) {
			return ItemKey.from(ASSET_PROVIDER_ID + PATH_SEPARATOR + bucketName + PATH_SEPARATOR);
		} else {
			return ItemKey.from(ASSET_PROVIDER_ID + PATH_SEPARATOR + bucketName + PATH_SEPARATOR + key);
		}
	}

	private class CloudinaryCacheEntry {

		/**
		 * Flag that specifies if children has been loaded for this cache entry.
		 */
		private boolean childrenLoaded;

		/**
		 * Cached item.
		 */
		private Item item;

		public CloudinaryCacheEntry(Item item) {
			this(item, false);
		}

		public CloudinaryCacheEntry(Item item, boolean childrenLoaded) {
			this.item = item;
			this.childrenLoaded = childrenLoaded;
		}

		private Item getItem() {
			return item;
		}

		private boolean isChildrenLoaded() {
			return childrenLoaded;
		}

		private void setChildrenLoaded(boolean childrenLoaded) {
			this.childrenLoaded = childrenLoaded;
		}
	}

	protected Iterator<Item> getChildren(Folder folder)  {
		try {

			List<Item> items = fetchFolders(folder);
			items.addAll(fetchFiles(folder));

			Collections.sort(items, (o1, o2) -> o1.getPath().compareTo(o2.getPath()));
			return items.iterator();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return IteratorUtils.emptyIterator();
	}

	private List<Item>  fetchFiles(Folder folder) {
		List<Item> items = Lists.newArrayList();

		try {
			ApiResponse folderFiles  = client().api().resources(ObjectUtils.asMap(
					"type", "upload",
					"prefix", encode(removeStart(folder.getPath(), PATH_SEPARATOR))));
			List resources = (List) folderFiles.get("resources");
			for (Object resource : resources) {
				Map r = (Map) resource;
				if (r.get("resource_type") != null && r.get("resource_type").equals("image"))  {
					String publicId = (String) r.get("public_id");
					String key = CloudinaryUtils.getObjectKeyFromPath(publicId);
					String bucket = CloudinaryUtils.getBucketNameFromPath(publicId);
					CloudinaryAsset asset = new CloudinaryAsset(this, createItemKeyForItem(key, bucket));
					items.add(asset);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return items;
	}

	private List<Item> fetchFolders(Folder folder) throws Exception {
		List<Item> items = Lists.newArrayList();

		ApiResponse response = client().api().subFolders(encode(folder.getPath()), ObjectUtils.asMap());
		List folders = (List) response.get("folders");

		for (Object dir : folders) {
			Map f = (Map) dir;
			String name = f.get("path").toString();
			items.add(new CloudinaryFolder(this, createItemKeyForItem(name, null)));
		}
		return items;
	}

	private Cloudinary client() {
		if (client == null) {
			client = service.get().getClient();
		}
		return client;
	}

	public static void main(String[] args) throws Exception {
		Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
				"cloud_name", "dandonov",
				"api_key", "118291969656326",
				"api_secret", "rGzQp9AEy1s3ByE73-mUoYhtz5E"));

		ApiResponse response = cloudinary.api().resources(ObjectUtils.asMap(
				"type", "upload",
				"prefix", "TestFolder/"));

		ApiResponse response1 = cloudinary.api().resources(ObjectUtils.asMap());

		ApiResponse response2 = cloudinary.api().rootFolders(ObjectUtils.asMap());
		ApiResponse response3 = cloudinary.api().subFolders("TestFolder", ObjectUtils.asMap());

		List folders = (List) response2.get("folders");

		for (Object folder : folders) {
			Map f = (Map) folder;
			String path = f.get("path").toString();
			String name = f.get("path").toString();

			ApiResponse folderFiles = cloudinary.api().resources(ObjectUtils.asMap(
					"type", "upload",
					"prefix", path + "/"));

			List resources = (List) folderFiles.get("resources");
			for (Object resource : resources) {
				Map r = (Map) resource;
				String rType = (String) r.get("resource_type");
				String publicId = (String) r.get("public_id");
				String uri = (String) r.get("url");
				System.out.println(">>>>" + rType + " " + uri);
			}
			ApiResponse dirFiles = cloudinary.api().subFolders(path, ObjectUtils.asMap());
		}

		System.out.println(response);
	}
}
