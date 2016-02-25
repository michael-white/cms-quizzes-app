package com.sharecare.cms.articles.activation.remote;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sharecare.cms.cloudinary.dam.CloudinaryClientServiceConnector;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.value.BinaryValue;

@Slf4j
public class CloudinaryArticlesAssetProcessor implements ArticleAssetProcessor {

	private final Cloudinary cloudinary;

	@Inject
	public CloudinaryArticlesAssetProcessor(CloudinaryClientServiceConnector clientServiceConnector) {
		this.cloudinary = clientServiceConnector.getClient();
	}

	@Override
	public Optional<ArticlesUploadResult> uploadAssetFrom(Node node) throws RepositoryException {

		NodeIterator it = node.getNodes(ArticleJCRSchema.image.name());
		if (!it.hasNext()) return Optional.empty();

		Node fileNode = it.nextNode();
		File file = extractFile(fileNode);
		log.debug("Uploading file {}", file.getName());

		try {

			Map map = cloudinary.uploader().upload(file, ObjectUtils.asMap("public_id", file.getName(),
																			"folder", "articles/",
																			"use_filename", true));
			ArticlesUploadResult result = new ArticlesUploadResult(map.get("public_id").toString() , map.get("url").toString());
			log.debug("File upload completed: {}", result.getUrl());
			return Optional.of(result);

		} catch (IOException e) {
			throw new RepositoryException("Unable to upload asset: " + e.getMessage());
		}
	}

	private File extractFile(Node fileNode) throws RepositoryException {
		String fileName = fileNode.getProperty("fileName").getValue().getString();
		String fileExtension = fileNode.getProperty("extension").getValue().getString();
		BinaryValue binary = (BinaryValue) fileNode.getProperty("jcr:data").getValue();
		try {
			File tempFile = File.createTempFile(fileName, fileExtension);
			tempFile.deleteOnExit();
			IOUtils.copy(binary.getStream(), new FileOutputStream(tempFile));
			return tempFile;
		} catch (IOException e) {
			throw new RepositoryException("Unable to upload asset: " + e.getMessage());
		}
	}
}
