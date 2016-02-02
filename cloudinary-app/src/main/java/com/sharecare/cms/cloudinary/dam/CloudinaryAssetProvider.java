package com.sharecare.cms.cloudinary.dam;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.dam.api.*;
import info.magnolia.dam.api.metadata.AssetMetadata;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.framework.message.MessagesManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;

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
        if (StringUtils.endsWith(itemKey.getAssetId(), PATH_SEPARATOR)) {
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
        if (StringUtils.endsWith(path, PATH_SEPARATOR)) {
            return getFolder(path);
        } else {
            return getAsset(path);
        }
    }

    @Override
    public Asset getAsset(String assetPath) throws PathNotFoundException {
        try {
            ApiResponse response = client().api().resource(assetPath, ObjectUtils.emptyMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (asset == null) {
//            throw new PathNotFoundException(assetPath);
//        }
        return new CloudinaryAsset();
    }

    @Override
    public Folder getFolder(String folderPath) throws PathNotFoundException {
        if (getRootFolder().getPath().equals(folderPath)) {
            return getRootFolder();
        }

        try {
            ApiResponse response =  client().api().resources(ObjectUtils.asMap(
					"prefix", folderPath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CloudinaryFolder();
    }

    @Override
    public Folder getRootFolder() {
        if (rootFolder == null) {
            try {
                ApiResponse response =  client().api().resources(ObjectUtils.asMap(
						"type", "upload",
						"prefix", "TestFolder/"));

            } catch (Exception e) {
                e.printStackTrace();
            }
            rootFolder = new CloudinaryFolder();
        }
        return rootFolder;
    }

    @Override
    public Iterator<Item> list(AssetQuery assetQuery) {
        return IteratorUtils.emptyIterator();
    }

    public void refresh() {
        cache.invalidateAll();
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

    private Cloudinary client() {
        if (client == null) {
            client = service.get().getClient();
        }
        return client;
    }

}
