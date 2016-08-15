package com.sharecare.cms.boot;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.security.ProtectionDomain;
import java.util.Map;

import com.sharecare.services.boot.EmbeddedServer;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.webapp.WebAppContext;

public class Server {
    private static final String CONTEXT_PATH = "/cmscentral";

    public static void main(String[] args) throws Exception {
        new EmbeddedServer()
                .withContextPath(CONTEXT_PATH)
                .buildServer()
                .start();
    }
}
