package com.sharecare.cms.articles.activation.remote;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import com.cloudinary.Cloudinary;
import com.cloudinary.ResponsiveBreakpoint;
import com.cloudinary.utils.ObjectUtils;
import com.sharecare.cms.articles.schema.ArticleJCRSchema;
import com.sharecare.cms.cloudinary.dam.CloudinaryClientServiceConnector;
import info.magnolia.dam.api.AssetProvider;
import info.magnolia.dam.api.AssetProviderRegistry;
import info.magnolia.dam.api.ItemKey;
import info.magnolia.dam.jcr.JcrAsset;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

@Slf4j
public class CloudinaryArticlesAssetProcessor implements ArticleAssetProcessor {

	private final Cloudinary cloudinary;
	private final AssetProvider jcrAssetProvider;

	@Inject
	public CloudinaryArticlesAssetProcessor(CloudinaryClientServiceConnector clientServiceConnector, AssetProviderRegistry providerRegistry) {
		this.jcrAssetProvider = providerRegistry.getProviderById("jcr");
		this.cloudinary = clientServiceConnector.getClient();
	}

	@Override
	public Optional<ArticlesUploadResult> uploadAssetFrom(Node node) throws RepositoryException {
		if (!node.hasProperty(ArticleJCRSchema.imageUpload.name()))
			return Optional.empty();

		Value value = node.getProperty(ArticleJCRSchema.imageUpload.name()).getValue();
		JcrAsset asset = (JcrAsset) jcrAssetProvider.getAsset(ItemKey.from(value.getString()));
		File file = extractFile(asset);
		log.debug("Uploading file {}", file.getName());

		try {
			String publicId = file.getName();
			if(publicId.contains("."))
				publicId = file.getName().substring(0, file.getName().lastIndexOf("."));

			Map map = cloudinary.uploader().upload(file, ObjectUtils.asMap(
					"public_id", publicId,
					"folder", "articles/",
					"use_filename", true,
					"responsive_breakpoints", new ResponsiveBreakpoint()
							.createDerived(true)
							.minWidth(60).maxWidth(600).maxImages(3)));
			ArticlesUploadResult result = new ArticlesUploadResult(map.get("public_id").toString(), map.get("url").toString());
			log.debug("File upload completed: {}", result.getUrl());
			return Optional.of(result);
		} catch (IOException e) {
			throw new RepositoryException("Unable to upload asset: " + e.getMessage());
		}
	}

	private File extractFile(JcrAsset jcrAsset) throws RepositoryException {

		try {
			File tempFile = new File("/tmp/" + jcrAsset.getName());
			tempFile.deleteOnExit();
			IOUtils.copy(jcrAsset.getContentStream(), new FileOutputStream(tempFile));
			return tempFile;
		} catch (IOException e) {
			throw new RepositoryException("Unable to upload asset: " + e.getMessage());
		}
	}
}
