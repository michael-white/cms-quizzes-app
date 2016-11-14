package com.sharecare.cms.slideshows.activation.remote;

import com.google.inject.Inject;
import com.sharecare.cms.cloudinary.dam.AssetUploadResult;
import com.sharecare.cms.slideshows.schema.SlideshowsJCRSchema;
import com.sharecare.sdk.slideshows.model.ByLine;
import com.sharecare.sdk.slideshows.model.OpenGraph;
import com.sharecare.sdk.slideshows.model.SlideRequest;
import com.sharecare.sdk.slideshows.model.SlideshowRequest;
import lombok.extern.slf4j.Slf4j;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import java.util.*;

import static com.sharecare.cms.utils.NodeUtils.*;

@Slf4j
public class RemoteSlideshowsRequestBuilder implements SlideshowsRequestBuilder {

    private final SlideshowsAssetProcessor slideshowsAssetProcessor;

    @Inject
    public RemoteSlideshowsRequestBuilder(SlideshowsAssetProcessor slideshowsAssetProcessor) {
        this.slideshowsAssetProcessor = slideshowsAssetProcessor;
    }

    @Override
    public SlideshowRequest forNode(Node node) throws RepositoryException {

        String uuid = node.hasNode(SlideshowsJCRSchema.legacyUUID.name())
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
                .build();

        return SlideshowRequest.builder()
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
                .build();
    }

    private Collection<String> fromCSV(String s) {
        return Arrays.asList(s.split(","));
    }

    private List<SlideRequest> processSlides(Node node) throws RepositoryException {

        List<SlideRequest> slides = new ArrayList<>();

        if (!node.hasNode(SlideshowsJCRSchema.slides.name()))
            return Collections.emptyList();


        NodeIterator iterator = node.getNode(SlideshowsJCRSchema.slides.name()).getNodes();
        while (iterator.hasNext()) {

            Node slide = iterator.nextNode();

            SlideRequest.SlideRequestBuilder slideBuilder = SlideRequest.builder()
                    .title(fromNode(SlideshowsJCRSchema.slideTitle.name(), slide))
                    .description(fromNode(SlideshowsJCRSchema.slideDescription.name(), slide))
                    .showAd(Boolean.valueOf(fromNode(SlideshowsJCRSchema.showAd.name(), slide)));

            Optional<AssetUploadResult> uploadResult = slideshowsAssetProcessor.uploadAssetFrom(slide);
            if (uploadResult.isPresent()) {
                slideBuilder.imageId(uploadResult.get().getId());
            }

            slides.add(slideBuilder.build());
        }

        return slides;
    }

}


