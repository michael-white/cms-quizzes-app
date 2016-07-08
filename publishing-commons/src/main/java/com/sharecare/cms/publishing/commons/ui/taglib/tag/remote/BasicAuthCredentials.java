package com.sharecare.cms.publishing.commons.ui.taglib.tag.remote;

public class BasicAuthCredentials {

    final private String username;
    final private String password;

    public BasicAuthCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
