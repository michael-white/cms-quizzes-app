package com.sharecare.cms.logging;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.JsonWritingUtils;
import net.logstash.logback.composite.loggingevent.MessageJsonProvider;

public class ApplicationLogJsonProvider extends MessageJsonProvider {

    @Override
    public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put(getFieldName(), event.getFormattedMessage());
        logEntry.put("level", event.getLevel().levelStr);
        logEntry.put("time", event.getTimeStamp() / 1000);  // UNIX time in seconds
        JsonWritingUtils.writeMapEntries(generator, logEntry);
    }

}