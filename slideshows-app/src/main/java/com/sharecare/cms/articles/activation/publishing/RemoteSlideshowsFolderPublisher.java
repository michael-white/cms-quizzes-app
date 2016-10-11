package com.sharecare.cms.articles.activation.publishing;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

@Slf4j
public class RemoteSlideshowsFolderPublisher implements RemoteDataPublisher {

	static final String NODE_TYPE = "mgnl:folder";

	private final RemoteSlideshowsPublisher remoteSlideshowsPublisher;

	@Inject
	public RemoteSlideshowsFolderPublisher(RemoteSlideshowsPublisher remoteSlideshowsPublisher) {
		this.remoteSlideshowsPublisher = remoteSlideshowsPublisher;
	}

	@Override
	public boolean publish(Node node, String environment) {
		try {
			NodeIterator nodeIterator = node.getNodes();
			while (nodeIterator.hasNext()) {
				boolean result = remoteSlideshowsPublisher.publish(nodeIterator.nextNode(), environment);
				if (!result) return false;
			}
		} catch (RepositoryException e) {
			log.error("Failed Activation of Folder  {} ", ExceptionUtils.getFullStackTrace(e));
			return false;
		}
		return true;
	}

	@Override
	public boolean unPublish(Node node, String environment) {
		try {
			NodeIterator nodeIterator = node.getNodes();
			while (nodeIterator.hasNext()) {
				boolean result = remoteSlideshowsPublisher.unPublish(nodeIterator.nextNode(), environment);
				if (!result) return false;
			}
		} catch (RepositoryException e) {
			log.error("Failed De-Activation of Folder  {} ", ExceptionUtils.getFullStackTrace(e));
			return false;
		}
		return true;
	}

	@Override
	public boolean canService(String nodeType) {
		return NODE_TYPE.equals(nodeType);
	}
}
