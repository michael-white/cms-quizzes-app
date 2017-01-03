package com.sharecare.cms.guides.activation.publishing;

import com.google.inject.Inject;
import com.sharecare.cms.cloudinary.dam.AssetUploadResult;
import com.sharecare.cms.guides.remote.HealthGuideRequestBuilder;
import com.sharecare.cms.guides.schema.HealthGuideJCRSchema;
import com.sharecare.healthguides.sdk.model.ContentRef;
import com.sharecare.healthguides.sdk.model.HealthGuideRequest;
import lombok.extern.slf4j.Slf4j;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static com.sharecare.cms.utils.NodeUtils.*;

@Slf4j
public class RemoteHealthGuideRequestBuilder implements HealthGuideRequestBuilder {

    @Inject
    public RemoteHealthGuideRequestBuilder() {
    }

    @Override
    public HealthGuideRequest forNode(Node node) throws RepositoryException {

       /* String uuid = node.hasNode(SlideshowsJCRSchema.legacyUUID.name())
                ? node.getProperty(SlideshowsJCRSchema.legacyUUID.name()).getString()
                : node.getIdentifier();

        ByLine byLine = ByLine.builder()
                .value(fromNode(SlideshowsJCRSchema.byline.name(), node))
                .option(fromNode(SlideshowsJCRSchema.bylineUrlOptionSelect.name(), node))
                .uri(fromNode(SlideshowsJCRSchema.bylineUrl.name(), node))
                .build();

        OpenGraph openGraph = OpenGraph.builder()
                .description(fromNode(SlideshowsJCRSchema.ogDescription.name(), node))
                .image(fromNode(SlideshowsJCRSchema.ogImage.name(), node))
                .label(fromNode(SlideshowsJCRSchema.ogLabel.name(), node))
                .title(fromNode(SlideshowsJCRSchema.ogTitle.name(), node))
                .type(fromNode(SlideshowsJCRSchema.ogTitle.name(), node))
                .url(fromNode(SlideshowsJCRSchema.ogUrl.name(), node))
                .build();*/

     /*   return SlideshowRequest.builder()
                .id(uuid)
                .uri(node.getName())
                .title(fromNode(SlideshowsJCRSchema.title.name(), node))
                .description(fromNode(SlideshowsJCRSchema.description.name(), node))
                .slides(processSlides(node))
                .pageAndMetaTitle(fromNode(SlideshowsJCRSchema.pageAndMetaTitle.name(), node))
                .keywords(fromCSV(fromNode(SlideshowsJCRSchema.metaKeywords.name(), node)))
                .disableSocial(Boolean.valueOf(fromNode(SlideshowsJCRSchema.disableSocial.name(), node)))
                .segments(extractMultiField(node, SlideshowsJCRSchema.segmentSelect.name()))
                .authors(extractMultiField(node, SlideshowsJCRSchema.authors.name()))
                .mentions(extractMultiField(node, SlideshowsJCRSchema.mentions.name()))
                .topicUri(fromNode(SlideshowsJCRSchema.topicUri.name(), node))
                .primaryTag(extractTag(SlideshowsJCRSchema.primaryTag.name(), node))
                .legacyUris(extractMultiField(node, SlideshowsJCRSchema.redirects.name()))
                .byLine(byLine)
                .type("slideshow")
                .openGraph(openGraph)
                .noIndexFollow(Boolean.valueOf(fromNode(SlideshowsJCRSchema.noIndexFollow.name(), node)))
                .canonicalReference(fromNode(SlideshowsJCRSchema.canonicalReference.name(), node))
                .build();*/



        return HealthGuideRequest.builder().build();
    }

}


