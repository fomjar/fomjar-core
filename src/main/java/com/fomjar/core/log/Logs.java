package com.fomjar.core.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;

public class Logs {

    public static Level level() {
        return Logs.level("root");
    }

    public static Level level(String packaje) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        return context.getLogger(packaje).getLevel();
    }

    public static void level(Level level) {
        Logs.level("root", level);
    }

    public static void level(String packaje, Level level) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(packaje).setLevel(level);
    }

}
