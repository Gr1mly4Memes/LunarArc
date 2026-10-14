package io.lunararcdevs.lunararc.common.server;

import java.util.logging.LogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LunarArcLogger extends java.util.logging.Logger {
    private final Logger slf4j;

    public static java.util.logging.Logger getLogger(String name) {
        return new LunarArcLogger(name);
    }

    private static boolean rootBridged;

    /**
     * Plugins that log through a plain java.util.logging logger (WorldGuard, for one) reach the JVM's root
     * ConsoleHandler, which prints a two-line stderr entry per record. Forward those records to the server log instead.
     */
    /** Chatty plugin loggers whose info/debug output is dropped from the console; warnings and errors still show. */
    private static final java.util.Set<String> NOISY_JUL_LOGGERS = java.util.Set.of(
            "com.sk89q.worldedit.extension.platform.PlatformCommandManager",
            "com.sk89q.worldguard.WorldGuard",
            "com.sk89q.worldguard.config.WorldConfiguration",
            "com.sk89q.worldguard.util.logging.RecordMessagePrefixer");

    /**
     * Netty probes for kqueue/epoll/io_uring natives at startup and logs the failure at debug level with
     * its exception. The debug log's extended stack-trace renderer then loads the failed native class by
     * name, which throws NoClassDefFoundError and prints an "exception occurred processing Appender"
     * block on every start. Raising these loggers above debug drops the pointless record.
     */
    public static void quietNettyNativeProbes() {
        try {
            Class<?> configurator = Class.forName("org.apache.logging.log4j.core.config.Configurator");
            Class<?> levelClass = Class.forName("org.apache.logging.log4j.Level");
            Object info = levelClass.getField("INFO").get(null);
            java.lang.reflect.Method setLevel = configurator.getMethod("setLevel", String.class, levelClass);
            for (String name : new String[] {
                    "io.netty.channel.kqueue.KQueue", "io.netty.channel.epoll.Epoll", "io.netty.channel.uring.IoUring"}) {
                setLevel.invoke(null, name, info);
            }
        } catch (Throwable ignored) {
            // log4j-core not present or a different logging backend; nothing to quiet.
        }
    }

    public static synchronized void bridgeRootLogger() {
        if (rootBridged) return;
        rootBridged = true;
        java.util.logging.Logger root = java.util.logging.LogManager.getLogManager().getLogger("");
        for (java.util.logging.Handler handler : root.getHandlers()) {
            if (handler instanceof java.util.logging.ConsoleHandler) root.removeHandler(handler);
        }
        java.util.logging.Formatter formatter = new java.util.logging.SimpleFormatter();
        root.addHandler(new java.util.logging.Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record == null || !isLoggable(record)) return;
                String loggerName = record.getLoggerName();
                if (loggerName != null && record.getLevel().intValue() < java.util.logging.Level.WARNING.intValue()
                        && NOISY_JUL_LOGGERS.contains(loggerName)) {
                    return;
                }
                Logger target = LoggerFactory.getLogger(loggerName == null || loggerName.isEmpty() ? "JUL" : loggerName);
                String message = formatter.formatMessage(record);
                Throwable thrown = record.getThrown();
                int level = record.getLevel().intValue();
                if (level >= java.util.logging.Level.SEVERE.intValue()) {
                    if (thrown != null) target.error(message, thrown); else target.error(message);
                } else if (level >= java.util.logging.Level.WARNING.intValue()) {
                    if (thrown != null) target.warn(message, thrown); else target.warn(message);
                } else if (level >= java.util.logging.Level.INFO.intValue()) {
                    if (thrown != null) target.info(message, thrown); else target.info(message);
                } else {
                    if (thrown != null) target.debug(message, thrown); else target.debug(message);
                }
            }

            @Override public void flush() { }

            @Override public void close() { }
        });
    }

    private LunarArcLogger(String name) {
        super(name, null);
        this.slf4j = LoggerFactory.getLogger(name);
    }

    @Override
    public void log(LogRecord record) {
        String msg = record.getMessage();
        Throwable ex = record.getThrown();
        java.util.logging.Level level = record.getLevel();

        if (level == java.util.logging.Level.SEVERE) {
            if (ex != null) slf4j.error(msg, ex); else slf4j.error(msg);
        } else if (level == java.util.logging.Level.WARNING) {
            if (ex != null) slf4j.warn(msg, ex); else slf4j.warn(msg);
        } else if (level == java.util.logging.Level.INFO) {
            if (ex != null) slf4j.info(msg, ex); else slf4j.info(msg);
        } else if (level == java.util.logging.Level.CONFIG || level == java.util.logging.Level.FINE) {
            if (ex != null) slf4j.debug(msg, ex); else slf4j.debug(msg);
        } else {
            if (ex != null) slf4j.trace(msg, ex); else slf4j.trace(msg);
        }
    }
}
