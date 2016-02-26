import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import com.sharecare.cms.logging.ApplicationLogJsonProvider
import net.logstash.logback.composite.loggingevent.LoggingEventJsonProviders
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.WARN

def MAG_LOG_LOCATION = System.getProperty("magnolia.log") != null ? System.getProperty("magnolia.log") : "/tmp/magnolia.log"

appender("stdout", ConsoleAppender) {
    file = "${APPLICATION_LOG_LOCATION}"

//    encoder(LoggingEventCompositeJsonEncoder) {
//        def jsprov = new LoggingEventJsonProviders()
//        jsprov.addMessage(new ApplicationLogJsonProvider())
//        providers=jsprov
//    }

    encoder(PatternLayoutEncoder) {
        pattern = "%level %logger - %msg%n"
    }

    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${MAG_LOG_LOCATION}/MAG_LOG.log.%d{.yyyy-MM-dd.HH-mm}"
    }
}

root(WARN, ["stdout"])
logger("com.sharecare", DEBUG, ["stdout"])
logger("org.eclipse.jetty", WARN, ["stdout"])