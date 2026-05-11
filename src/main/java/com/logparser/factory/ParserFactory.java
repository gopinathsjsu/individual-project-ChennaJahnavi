package com.logparser.factory;

import com.logparser.classifier.LogType;
import com.logparser.parsers.APMLogParser;
import com.logparser.parsers.ApplicationLogParser;
import com.logparser.parsers.ILogParser;
import com.logparser.parsers.RequestLogParser;

import java.util.EnumMap;
import java.util.Map;

/**
 * Creates (or supplies) parser strategies per {@link LogType}. One instance per type for a single run.
 */
public class ParserFactory {

    public Map<LogType, ILogParser> createParsersForRun() {
        Map<LogType, ILogParser> parsers = new EnumMap<>(LogType.class);
        parsers.put(LogType.APM, new APMLogParser());
        parsers.put(LogType.APPLICATION, new ApplicationLogParser());
        parsers.put(LogType.REQUEST, new RequestLogParser());
        return parsers;
    }

    public ILogParser create(LogType type, Map<LogType, ILogParser> runParsers) {
        if (type == null || type == LogType.UNKNOWN) {
            throw new IllegalArgumentException("type");
        }
        ILogParser p = runParsers.get(type);
        if (p == null) {
            throw new IllegalArgumentException("No parser for " + type);
        }
        return p;
    }
}
