package com.sharecare.cms.slideshows.activation.remote;

import com.google.common.collect.Lists;
import com.sharecare.cms.slideshows.activation.remote.sdk.SlideshowsRequest;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
public class RemoteSlideshowsRequestBuilder implements SlideshowsRequestBuilder {

    @Override
    public List<SlideshowsRequest> forNode(Node node, Optional<ArticlesUploadResult> uploadResult) throws RepositoryException {
        return Lists.newArrayList();
    }

}


