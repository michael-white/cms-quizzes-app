import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import com.sharecare.cms.logging.ApplicationLogJsonProvider
import net.logstash.logback.composite.loggingevent.LoggingEventJsonProviders
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.ERROR
import static ch.qos.logback.classic.Level.INFO

if ("dev".equals(System.getProperty("env"))) {
    appender("APP_LOG", ConsoleAppender) {
        encoder(PatternLayoutEncoder) {
            pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
        }
    }

    root(INFO, ["APP_LOG"])
    logger("com.sharecare", DEBUG, ["APP_LOG"])
    //logger("org.eclipse.jetty", WARN, ["stdout"])

} else {
    appender("APP_LOG", FileAppender) {
        file = System.getProperty("app.log")

        encoder(LoggingEventCompositeJsonEncoder) {
            def jsprov = new LoggingEventJsonProviders()
            jsprov.addMessage(new ApplicationLogJsonProvider())
            providers = jsprov
        }
    }

    root(DEBUG, ["APP_LOG"])
    logger("com.sharecare", DEBUG, ["APP_LOG"])
}




