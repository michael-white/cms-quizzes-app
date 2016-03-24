import ch.qos.logback.core.ConsoleAppender
import com.sharecare.cms.logging.ApplicationLogJsonProvider
import net.logstash.logback.composite.loggingevent.LoggingEventJsonProviders
import net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder

import static ch.qos.logback.classic.Level.*

appender("stdout", ConsoleAppender) {
    encoder(LoggingEventCompositeJsonEncoder) {
        def jsprov = new LoggingEventJsonProviders()
        jsprov.addMessage(new ApplicationLogJsonProvider())
        providers=jsprov
    }
}

root(WARN, ["stdout"])
logger("com.sharecare", DEBUG, ["stdout"])
logger("info.magnolia", ERROR, ["stdout"])
logger("org.eclipse.jetty", WARN, ["stdout"])