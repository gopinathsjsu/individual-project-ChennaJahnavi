package com.logparser.parsers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ApplicationLogParserTest {

    @Test
    void countsUppercaseLevels() {
        ApplicationLogParser p = new ApplicationLogParser();
        p.parse("level=error x=1");
        p.parse("level=ERROR x=2");
        p.parse("level=info x=3");
        var agg = p.aggregate();
        assertEquals(2, ((Number) agg.get("ERROR")).intValue());
        assertEquals(1, ((Number) agg.get("INFO")).intValue());
    }

    @Test
    void rejectsMissingLevel() {
        ApplicationLogParser p = new ApplicationLogParser();
        assertFalse(p.parse("message=only"));
        assertEquals(0, p.aggregate().size());
    }
}
