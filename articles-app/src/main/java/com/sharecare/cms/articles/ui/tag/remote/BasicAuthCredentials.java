package com.sharecare.cms.articles.ui.tag.remote;

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
