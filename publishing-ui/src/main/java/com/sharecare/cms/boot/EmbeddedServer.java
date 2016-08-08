package com.sharecare.cms.boot;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.security.ProtectionDomain;
import java.util.Map;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.webapp.WebAppContext;

public class EmbeddedServer {

    public static void main(String[] args) throws Exception {

        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        Map<String,String> inputArguments = bean.getSystemProperties();

        Server server = new Server(serverPort(inputArguments));

        MBeanContainer mbContainer=new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
        server.addEventListener(mbContainer);
        server.addBean(mbContainer);

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setContextPath("/cmscentral");

        ProtectionDomain protectionDomain = EmbeddedServer.class.getProtectionDomain();
        webAppContext.setWar(protectionDomain.getCodeSource().getLocation().toExternalForm());

        server.setHandler(webAppContext);

        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        handlers.setHandlers(new Handler[]{webAppContext, contexts,new DefaultHandler(),requestLogHandler});

        NCSARequestLog requestLog = initAccessLog(inputArguments);
        requestLogHandler.setRequestLog(requestLog);

        server.setHandler(handlers);

        server.start();
        server.join();
    }


    private static NCSARequestLog initAccessLog(Map<String, String> inputArguments) {
        NCSARequestLog requestLog = new AsyncJSONRequestLog();
        requestLog.setRetainDays(90);
        requestLog.setAppend(true);
        requestLog.setExtended(false);
        requestLog.setLogTimeZone("GMT");
        requestLog.setFilename(accesslog(inputArguments));
        return requestLog;
    }

    private static String accesslog(Map<String, String> systemArgs) {
        for (Map.Entry<String,String> arg : systemArgs.entrySet()) {
            if (arg.getKey().equals("access.log")) {
                return arg.getValue();
            }
        }
        return "access.log";
    }


    private static int serverPort(Map<String,String> systemArgs) {
        for (Map.Entry<String,String> arg : systemArgs.entrySet()) {
            if (arg.getKey().equals("server.port")) {
                return Integer.parseInt(arg.getValue());
            }
        }
        return 8080;
    }
}
