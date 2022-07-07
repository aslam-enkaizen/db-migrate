package com.exrade.util;

import com.exrade.core.ExLogger;

/**
 * @author Rhidoy
 * @created 6/21/22
 */
public class Logger extends ExLogger {

    public static void info(String message) {
        get().info(message);
    }

    public static void info(String message, Throwable throwable) {
        get().info(message, throwable);
    }

    public static void warn(String message) {
        get().warn(message);
    }

    public static void warn(String message, Throwable throwable) {
        get().warn(message, throwable);
    }

    public static void error(String message) {
        get().error(message);
    }

    public static void error(String message, Throwable throwable) {
        get().error(message, throwable);
    }
    public static void error(String message, String throwable) {
        get().error(message, throwable);
    }
    public static void debug(String message, String throwable) {
        get().error(message, throwable);
    }
}
