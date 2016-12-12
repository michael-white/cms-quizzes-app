package com.sharecare.cms.featured.schema;

public enum FeaturedJCRSchema {
    hero,
    carousel;

    public static FeaturedJCRSchema forName(String field) {
        for(FeaturedJCRSchema v : values()) {
            if (v.name().equals(field))
                return v;
        }
        return null;
    }

}
