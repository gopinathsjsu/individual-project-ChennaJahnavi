package com.logparser.classifier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultLogClassifierTest {

    private final DefaultLogClassifier classifier = new DefaultLogClassifier();

    @Test
    void classifiesRequestBeforeOthersWhenAllKeysPresent() {
        String line =
                "timestamp=t request_method=GET request_url=\"/x\" response_status=200 response_time_ms=1 metric=m value=1 level=INFO";
        assertEquals(LogType.REQUEST, classifier.classify(line));
    }

    @Test
    void classifiesApmWhenMetricAndValue() {
        assertEquals(LogType.APM, classifier.classify("metric=cpu_usage_percent value=10"));
    }

    @Test
    void classifiesApplicationWhenLevelOnly() {
        assertEquals(LogType.APPLICATION, classifier.classify("level=WARN message=hi"));
    }

    @Test
    void unknownForGarbage() {
        assertEquals(LogType.UNKNOWN, classifier.classify("not a log line"));
        assertEquals(LogType.UNKNOWN, classifier.classify(""));
    }
}
