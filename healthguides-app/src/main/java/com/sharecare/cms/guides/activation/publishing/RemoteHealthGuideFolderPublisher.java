package com.sharecare.cms.guides.activation.publishing;

import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

@Slf4j
public class RemoteHealthGuideFolderPublisher implements RemoteDataPublisher {

	static final String NODE_TYPE = "mgnl:folder";

	private final RemoteHealthGuidePublisher publisher;

	@Inject
	public RemoteHealthGuideFolderPublisher(RemoteHealthGuidePublisher publisher) {
		this.publisher = publisher;
	}

	@Override
	public boolean publish(Node node, String environment) {
		try {
			NodeIterator nodeIterator = node.getNodes();
			while (nodeIterator.hasNext()) {
				boolean result = publisher.publish(nodeIterator.nextNode(), environment);
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
				boolean result = publisher.unPublish(nodeIterator.nextNode(), environment);
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
