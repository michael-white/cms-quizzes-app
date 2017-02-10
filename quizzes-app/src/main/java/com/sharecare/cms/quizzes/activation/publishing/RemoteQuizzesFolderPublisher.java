package com.sharecare.cms.quizzes.activation.publishing;

import javax.inject.Inject;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;

import com.sharecare.cms.publishing.commons.activation.RemoteDataPublisher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;

@Slf4j
public class RemoteQuizzesFolderPublisher implements RemoteDataPublisher {

    static final String NODE_TYPE = "mgnl:folder";

    private final RemoteQuizzesPublisher remoteQuizzesPublisher;

    @Inject
    public RemoteQuizzesFolderPublisher(RemoteQuizzesPublisher remoteQuizzesPublisher) {
        this.remoteQuizzesPublisher = remoteQuizzesPublisher;
    }

    @Override
    public boolean publish(Node node, String environment) {
        try {
            NodeIterator nodeIterator = node.getNodes();
            while (nodeIterator.hasNext()) {
                boolean result = remoteQuizzesPublisher.publish(nodeIterator.nextNode(), environment);
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
                boolean result = remoteQuizzesPublisher.unPublish(nodeIterator.nextNode(), environment);
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
