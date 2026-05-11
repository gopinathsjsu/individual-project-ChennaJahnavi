package com.logparser.parsers;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RequestLogParserTest {

    @Test
    void p95NearestRankSingleSample() {
        assertEquals(200, RequestLogParser.percentileNearestRank95(List.of(200)));
    }

    @Test
    void p95NearestRankTwentySamples() {
        List<Integer> sorted = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 100);
        int r = (int) Math.ceil(0.95 * sorted.size());
        assertEquals(19, r);
        assertEquals(19, RequestLogParser.percentileNearestRank95(sorted));
    }

    @Test
    void aggregatesPerRoute() {
        RequestLogParser p = new RequestLogParser();
        p.parse("request_method=GET request_url=\"/a\" response_status=200 response_time_ms=100");
        p.parse("request_method=GET request_url=\"/a\" response_status=500 response_time_ms=300");
        @SuppressWarnings("unchecked")
        Map<String, Object> route = (Map<String, Object>) p.aggregate().get("/a");
        @SuppressWarnings("unchecked")
        Map<String, Object> rt = (Map<String, Object>) route.get("response_times");
        assertEquals(100, rt.get("min"));
        assertEquals(300, rt.get("max"));
        assertEquals(300, rt.get("95_percentile"));
        @SuppressWarnings("unchecked")
        Map<String, Object> sc = (Map<String, Object>) route.get("status_codes");
        assertEquals(1, sc.get("2XX"));
        assertEquals(0, sc.get("4XX"));
        assertEquals(1, sc.get("5XX"));
    }

    @Test
    void rejectsBadNumbers() {
        RequestLogParser p = new RequestLogParser();
        assertFalse(
                p.parse(
                        "request_method=GET request_url=\"/a\" response_status=abc response_time_ms=1"));
    }
}
