package com.sharecare.cms.featured.schema;

public enum FeaturedQuizJCRSchema {
    hero,
    carousel;

    public static FeaturedQuizJCRSchema forName(String field) {
        for(FeaturedQuizJCRSchema v : values()) {
            if (v.name().equals(field))
                return v;
        }
        return null;
    }

}
