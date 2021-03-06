package com.sharecare.cms.articles.activation.remote;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.sharecare.articles.sdk.model.ArticleRequest;
import com.sharecare.cms.articles.schema.ArticleJCRSchema;
import com.sharecare.cms.cloudinary.dam.AssetUploadResult;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.PrimaryTagField;
import com.sharecare.cms.publishing.commons.ui.taglib.tag.SecondaryTagField;
import com.sharecare.core.sdk.model.Tag;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import javax.jcr.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
public class RemoteArticleRequestBuilder implements ArticleRequestBuilder {

    private static final Pattern LOCALE_PATTERN = Pattern.compile("(\\w{2})_(\\w+)");

    private enum Locale {
        en
    }

    @Override
    public List<ArticleRequest> forNode(Node node, Optional<AssetUploadResult> uploadResult) throws RepositoryException {
        Map<String, ArticleRequest.ArticleRequestBuilder> localeArticles = initArticleLocaleMap(node, uploadResult);

        processProperties(node, localeArticles);
        return localeArticles.values()
                             .stream()
                             .map(ArticleRequest.ArticleRequestBuilder::build)
                             .collect(toList());
    }

    private void processProperties(Node n, Map<String, ArticleRequest.ArticleRequestBuilder> localeArticles) throws RepositoryException {
        if (!n.hasProperty("expirationDate")) {
            n.setProperty("expirationDate", "");
        }
        PropertyIterator it = n.getProperties();
        while (it.hasNext()) {
            Property p = it.nextProperty();
            final String field = p.getName();
            Matcher m = LOCALE_PATTERN.matcher(field);

            if (p.isMultiple()) {
                Value[] values = p.getValues();
                List<String> valueList = new ArrayList<>(values.length);
                for (Value v : values)
                    valueList.add(v.getString());

                if (m.find()) {
                    ArticleRequest.ArticleRequestBuilder builder = localeArticles.get(m.group(1));
                    if (builder != null)
                        populateBuilderMulti(builder, m.group(2), valueList);
                } else {
                    localeArticles.forEach((k, v) -> populateBuilderMulti(v, field, valueList));
                }
            } else {
                final String value = p.getString();
                if (m.find()) {
                    ArticleRequest.ArticleRequestBuilder builder = localeArticles.get(m.group(1));
                    if (builder != null)
                        populateBuilder(builder, m.group(2), value);
                } else {
                    localeArticles.forEach((k, v) -> populateBuilder(v, field, value));
                }
            }
        }
    }

    private Map<String, ArticleRequest.ArticleRequestBuilder> initArticleLocaleMap(Node node, Optional<AssetUploadResult> uploadResult) throws RepositoryException {

        Map<String, ArticleRequest.ArticleRequestBuilder> map = Maps.newHashMap();

        String uuid = node.hasNode(ArticleJCRSchema.legacyUUID.name()) ? node.getProperty(ArticleJCRSchema.legacyUUID.name()).getString() : node.getIdentifier();

        for (Locale l : Locale.values()) {
            ArticleRequest.ArticleRequestBuilder builder = ArticleRequest.builder()
                                                    .id(uuid)
                                                    .articleUri(node.getName())
                                                    .locale(l.name());

            if (uploadResult.isPresent()) {
                AssetUploadResult ur = uploadResult.get();
                builder.imageUrl(ur.getUrl())
                       .imageId(ur.getId());
            }

            map.put(l.name(), builder);
        }
        return map;
    }

    private void populateBuilder(ArticleRequest.ArticleRequestBuilder builder, String field, String value) {

        if (builder != null) {
        if (PrimaryTagField.PRIMARY_TAG_FIELD.equals(field)) {
            builder.primaryTag(new Tag(value, "tag"));
        } else {
            ArticleJCRSchema fieldName = ArticleJCRSchema.forName(field);
            if (fieldName == null) return;

            switch (fieldName) {
                case body:
                    builder.body(value);
                    break;
                case title:
                    builder.title(value);
                    break;
                case subHead:
                    builder.subHead(value);
                    break;
                case topicUri:
                    builder.topicUri(value);
                    break;
                case bylineUrl:
                    builder.byLineUri(value);
                    break;
                case byline:
                    builder.byLine(value);
                    break;
                case bylineUrlOptionSelect:
                    builder.byLineOption(value);
                    break;
                case realAgeOptionSelect:
                    builder.realAge(Boolean.valueOf(value));
                    break;
                case callOutBody:
                    builder.callOutBody(value);
                    break;
                case videoId:
                    builder.videoId(value);
                    break;
                case playerId:
                    builder.playerId(value);
                    break;
                case videoTitle:
                    builder.videoTitle(value);
                case videoTeaser:
                    builder.videoTeaser(value);
                    break;
                case pageAndMetaTitle:
                    builder.metaTitle(Collections.singletonList(value));
                    break;
                case metaDescription:
                    builder.metaDescription(Collections.singletonList(value));
                    break;
                case metaKeywords:
                    builder.keywords(Splitter.on(",").splitToList(value));
                    break;
                case hasSynviscComScore:
                    builder.hasSynviscComScore(Boolean.valueOf(value));
                    break;
                case ogLabel:
                    builder.ogLabel(value);
                    break;
                case disableSocial:
                    builder.disableSocialButtons(Boolean.valueOf(value));
                    break;
                case ogType:
                    builder.ogType(value);
                    break;
                case ogImage:
                    builder.ogImage(value);
                    break;
                case ogTitle:
                    builder.ogTitle(value);
                    break;
                case ogDescription:
                    builder.ogDescription(value);
                    break;
                case ogUrl:
                    builder.ogUrl(value);
                    break;
                case noIndexFollow:
                    builder.noIndexFollow(Boolean.valueOf(value));
                    break;
                case canonicalReference:
                    builder.canonicalReference(value);
                    break;
                case contentFlags:
                    builder.contentFlags(Collections.singletonList(value));
                    break;
                case propensityScore:
                    builder.propensityScore(Long.parseLong(StringUtils.defaultIfBlank(value, "0")));
                    break;
                case expirationDate:
                    long expirationDate = Long.MAX_VALUE;
                    if (StringUtils.isNotEmpty(value)) {
                         expirationDate = new DateTime(value).getMillis();
                    }
                    builder.expirationDate(expirationDate);
                    break;
                default:
                    break;

            }
            }
        }
    }

    private void populateBuilderMulti(ArticleRequest.ArticleRequestBuilder builder, String field, List<String> values) {

        if (builder != null) {
            if (SecondaryTagField.SECONDARY_TAG_FIELD.equals(field)) {
                builder.secondaryTags(values.stream().map(v -> new Tag(v, "tag")).collect(Collectors.toList()));
            } else {

                ArticleJCRSchema fieldName = ArticleJCRSchema.forName(field);
                if (fieldName == null) return;

                switch (fieldName) {
                    case segmentSelect:
                        builder.segments(values);
                        break;
                    case contentFlags:
                        builder.contentFlags(values);
                        break;
                    case mentions:
                        builder.mentions(values);
                        break;
                    case authors:
                        builder.authors(values);
                        break;
                    case redirects:
                        builder.legacyUris(values);
                        break;
                    case livingInTheGreenScale:
                        builder.livingInTheGreenScale(values.stream()
                                .mapToInt(Integer::parseInt)
                                .boxed()
                                .collect(Collectors.toList()));
                    default:
                        break;
                }
            }
        }
    }
}


