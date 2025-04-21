package com.pixelservices.mobot.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.spi.ThrowableProxy;

public class CustomLogbackAppender extends AppenderBase<ILoggingEvent> {
    private final Logger customLogger = LoggerFactory.getLogger("Internal");

    @Override
    public void start() {
        super.start();
        customLogger.info("Hooked into Logback");
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!isStarted()) return;

        switch (eventObject.getLevel().levelStr) {
            case "WARN":
                customLogger.warn(eventObject.getFormattedMessage());
                break;
            case "ERROR":
                Throwable throwable = eventObject.getThrowableProxy() != null
                        ? ((ThrowableProxy) eventObject.getThrowableProxy()).getThrowable()
                        : null;
                customLogger.error(eventObject.getFormattedMessage(), throwable);
                break;
            case "DEBUG":
                customLogger.debug(eventObject.getFormattedMessage());
                break;
            default:
                customLogger.info(eventObject.getFormattedMessage());
                break;
        }
    }
}
