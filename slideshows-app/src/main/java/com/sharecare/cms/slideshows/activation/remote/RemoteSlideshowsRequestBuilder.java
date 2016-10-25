package com.sharecare.cms.slideshows.activation.remote;

import com.google.common.collect.Lists;
import com.sharecare.cms.cloudinary.dam.AssetUploadResult;
import com.sharecare.slideshows.sdk.model.SlideshowRequest;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.List;

@Slf4j
public class RemoteSlideshowsRequestBuilder implements SlideshowsRequestBuilder {

    @Override
    public List<SlideshowRequest> forNode(Node node, List<AssetUploadResult> uploadResult) throws RepositoryException {
        return Lists.newArrayList();
    }

}


