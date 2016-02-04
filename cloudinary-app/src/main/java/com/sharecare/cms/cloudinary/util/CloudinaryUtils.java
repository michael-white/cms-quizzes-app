/**
 * This file Copyright (c) 2015 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This program and the accompanying materials are made
 * available under the terms of the Magnolia Network Agreement
 * which accompanies this distribution, and is available at
 * http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package com.sharecare.cms.cloudinary.util;

import static com.sharecare.cms.cloudinary.dam.CloudinaryAssetProvider.*;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility methods for Cloudinary connector.
 */
public class CloudinaryUtils {

    private CloudinaryUtils() {
    }

    public static String getBucketNameFromPath(String path) {
        return StringUtils.substringBefore(StringUtils.removeStart(path, PATH_SEPARATOR), PATH_SEPARATOR);
    }

    public static String getObjectKeyFromPath(String path) {
        return StringUtils.substringAfter(StringUtils.removeStart(path, PATH_SEPARATOR), PATH_SEPARATOR);
    }

    public static String getFullPath(String bucketName, String key) {
        if (StringUtils.isBlank(key)) {
            return PATH_SEPARATOR + bucketName + PATH_SEPARATOR;
        }
        return PATH_SEPARATOR + bucketName + PATH_SEPARATOR + key;
    }
}
