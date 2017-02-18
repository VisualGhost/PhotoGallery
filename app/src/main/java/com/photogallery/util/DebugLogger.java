package com.photogallery.util;


import android.util.Log;

public class DebugLogger {

    private static boolean sIsDebug;

    // private
    private DebugLogger() {
        // empty
    }

    // private
    private DebugLogger(boolean isDebug) {
        sIsDebug = isDebug;
    }

    public static class DebugLoggerHolder {
        private static DebugLogger sDebugLogger = new DebugLogger(true);
    }

    /**
     * Enable logging
     */
    public static void enableLogging() {
        DebugLogger debugLogger = DebugLoggerHolder.sDebugLogger;
        DebugLogger.d(DebugLogger.class.getSimpleName(), debugLogger + " is enabled for logging");
    }

    private static final String NULL = "NULL";

    public static void d(String tag, String message) {
        if (sIsDebug) {
            Log.d(getTag(tag), message != null ? message : NULL);
        }
    }

    private static String getTag(String tag) {
        return String.format("%s : < " + tag + " >", DebugLogger.class.getSimpleName());
    }

    public static void e(String tag, String message) {
        if (sIsDebug) {
            Log.e(getTag(tag), message != null ? message : NULL);
        }
    }

    public static void i(String tag, String message) {
        if (sIsDebug) {
            Log.i(getTag(tag), message != null ? message : NULL);
        }
    }

}
