package com.logparser.factory;

import com.logparser.classifier.LogType;
import com.logparser.parsers.APMLogParser;
import com.logparser.parsers.ApplicationLogParser;
import com.logparser.parsers.ILogParser;
import com.logparser.parsers.RequestLogParser;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParserFactoryTest {

    @Test
    void createsDistinctParserInstancesPerRun() {
        ParserFactory f = new ParserFactory();
        Map<LogType, ILogParser> a = f.createParsersForRun();
        Map<LogType, ILogParser> b = f.createParsersForRun();
        assertInstanceOf(APMLogParser.class, a.get(LogType.APM));
        assertInstanceOf(ApplicationLogParser.class, a.get(LogType.APPLICATION));
        assertInstanceOf(RequestLogParser.class, a.get(LogType.REQUEST));
        a.get(LogType.APM).parse("metric=m value=1");
        assertNotNull(a.get(LogType.APM).aggregate().get("m"));
        assertNull(b.get(LogType.APM).aggregate().get("m"));
    }

    @Test
    void createRejectsUnknown() {
        ParserFactory f = new ParserFactory();
        assertThrows(IllegalArgumentException.class, () -> f.create(LogType.UNKNOWN, f.createParsersForRun()));
    }
}
