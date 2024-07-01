package com.github.test.util;

import java.util.logging.Logger;

public class LoggerUtil implements FormattedLogger {
    private final Logger logger;

    public LoggerUtil(Logger logger) {
        this.logger = logger;
    }

    public void info(String messageFormat, Object... args) {
        String message = String.format(messageFormat, args);
        this.logger.info(message);
    }

    public void warn(String messageFormat, Object... args) {
        String message = String.format(messageFormat, args);
        this.logger.warning(message);
    }

    public void error(String messageFormat, Object... args) {
        String message = String.format(messageFormat, args);
        this.logger.severe(message);
    }
}
