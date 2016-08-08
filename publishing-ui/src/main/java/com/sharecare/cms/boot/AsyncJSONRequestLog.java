package com.sharecare.cms.boot;

import com.sharecare.platform.xflow.HeaderValue;
import org.eclipse.jetty.server.AsyncNCSARequestLog;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

public class AsyncJSONRequestLog extends AsyncNCSARequestLog {


    protected static final Logger LOG = Log.getLogger(AsyncJSONRequestLog.class);

    @Override
    public void log(Request request, Response response) {

        long curTime = System.currentTimeMillis();

        LogEntry logEntry = new LogEntry();
        logEntry.sourceip = request.getLocalAddr();
        logEntry.user = getUserFromAuth(request);
        logEntry.time = curTime / 1000; // UNIX time in seconds
        logEntry.request = request.getMethod() + " " + request.getUri().toString() + " " + request.getProtocol();
        logEntry.status = getStatus(response);
        logEntry.size = response.getBufferSize();
        logEntry.useragent = fetchOrDefault(request.getHeader("User-Agent"));
        logEntry.clientip = fetchOrDefault(request.getRemoteAddr());
        logEntry.reqhost = fetchOrDefault(request.getLocalName());
        logEntry.speed = curTime - request.getTimeStamp();
        logEntry.flowid = fetchOrDefault(request.getHeader(HeaderValue.HEADER_NAME));
        logEntry.reqproto = request.getProtocol();
        logEntry.referer = fetchOrDefault(request.getHeader("Referer"));


        try {

            write(logEntry.toString());

        } catch (IOException io) {
            LOG.warn(io);
        }
    }


    // TODO Implement for the user auth
    public String getUserFromAuth(Request request) {
        final String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Basic")) {
            String base64Credentials = authorization.substring("Basic".length()).trim();
            String credentials = new String(DatatypeConverter.parseBase64Binary(base64Credentials));
            final String[] values = credentials.split(":", 2);
            return values[0];
        }
        return "-";
    }

    private String fetchOrDefault(String header) {
        return header != null && !header.equals("") ? header : "-";
    }

    private int getStatus(Response response) {

        int status = response.getStatus();
        if (status <= 0)
            status = 404;

        return status;
    }


    private static class LogEntry {
        String sourceip;
        String user;
        long time;
        String request;
        int status;
        int size;
        String useragent;
        String clientip;
        String reqhost;
        long speed;
        String flowid;
        String reqproto;
        String referer;

        @Override
        public String toString() {
            return "{\"sourceip\":\"" + sourceip + "\",\"user\":\"" + user + "\",\"time\":\"" + time + "\", \"request\":\"" + request + "\",\"status\":\"" + status + "\",\"size\":\"" + size + "\",\"useragent\":\"" + useragent + "\",\"clientip\":\"" + clientip + "\",\"reqhost\":\"" + reqhost + "\",\"speed\":\"" + speed + "\",\"flowid\":\"" + flowid + "\",\"reqproto\":\"" + reqproto + "\",\"referer\":\"" + referer + "\" }";
        }
    }


}


