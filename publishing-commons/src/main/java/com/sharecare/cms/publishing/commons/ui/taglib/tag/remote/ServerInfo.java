package com.sharecare.cms.publishing.commons.ui.taglib.tag.remote;

public class ServerInfo {


    final private String protocol;
    final private String hostName;
    final private int port;

    public ServerInfo(String protocol, String hostName, int port) {
        this.protocol = protocol;
        this.hostName = hostName;
        this.port = port;
    }

    public String toBaseUrl(){
        return port != 80 ?  String.format("%s://%s:%d", protocol, hostName, port) : String.format("%s://%s", protocol, hostName);
    }
}
