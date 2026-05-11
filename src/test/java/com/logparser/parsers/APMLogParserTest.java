package com.logparser.parsers;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class APMLogParserTest {

    @Test
    void rejectsNonNumericValue() {
        APMLogParser p = new APMLogParser();
        assertFalse(p.parse("metric=x value=nan"));
        assertTrue(p.aggregate().isEmpty());
    }

    @Test
    void medianOddAndEvenCounts() {
        APMLogParser p = new APMLogParser();
        p.parse("metric=m value=1");
        p.parse("metric=m value=2");
        p.parse("metric=m value=3");
        @SuppressWarnings("unchecked")
        Map<String, Object> mStats = (Map<String, Object>) p.aggregate().get("m");
        assertEquals(1L, mStats.get("minimum"));
        assertEquals(2L, mStats.get("median"));
        assertEquals(2L, mStats.get("average"));
        assertEquals(3L, mStats.get("max"));

        APMLogParser p2 = new APMLogParser();
        p2.parse("metric=x value=10");
        p2.parse("metric=x value=20");
        @SuppressWarnings("unchecked")
        Map<String, Object> xStats = (Map<String, Object>) p2.aggregate().get("x");
        assertEquals(15.0, ((Number) xStats.get("median")).doubleValue());
    }
}
