package com.sharecare.cms.publishing.commons.utils;

import com.sharecare.core.sdk.model.Tag;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class NodeUtils {

    public static List<String> extractMultiField(Node node, String field) throws RepositoryException {
        List<String> values = new ArrayList<>();
        if (node.hasProperty(field)) {
            Property p = node.getProperty(field);
            if (p.isMultiple()) {
                for (Value author : p.getValues()) {
                    values.add(author.getString());
                }
            }
        }
        return  values;
    }

    public static String fromNode(String name, Node node) {

        try {
            Property property = node.getProperty(name);
            return property.getValue().getString();
        } catch (RepositoryException e) {
            log.error(e.getMessage());
            return "";
        }
    }

    public static Tag extractTag(String name, Node node) {
        String tagId = fromNode(name, node);
        return new Tag(tagId, "tag");
    }

    public static long extractDateInMillis(String value) {
        long dateInMillis = Long.MAX_VALUE;
        if (StringUtils.isNotEmpty(value)) {
            dateInMillis = new DateTime(value).getMillis();
        }

        return dateInMillis;
    }

}

