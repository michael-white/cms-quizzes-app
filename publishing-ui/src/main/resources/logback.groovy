import ch.qos.logback.core.FileAppender
import com.sharecare.cms.logging.ApplicationLogJsonProvider
import net.logstash.logback.composite.loggingevent.LoggingEventJsonProviders
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder

import static ch.qos.logback.classic.Level.DEBUG
import static ch.qos.logback.classic.Level.ERROR

def APPLICATION_LOG_LOCATION = System.getProperty("app.log") != null ? System.getProperty("app.log") : "/tmp/magnolia.log"

appender("APP_LOG", FileAppender) {
    file = "${APPLICATION_LOG_LOCATION}"

    encoder(LoggingEventCompositeJsonEncoder) {
        def jsprov = new LoggingEventJsonProviders()
        jsprov.addMessage(new ApplicationLogJsonProvider())
        providers=jsprov
    }
}

root(ERROR, ["APP_LOG"])
logger("com.sharecare", DEBUG, ["APP_LOG"])
//logger("org.eclipse.jetty", WARN, ["stdout"])