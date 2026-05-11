package com.logparser.classifier;

import com.logparser.util.KeyValueLineParser;

import java.util.Map;

/**
 * Classifies lines by required key sets. Order: REQUEST (most specific), APM, APPLICATION.
 */
public class DefaultLogClassifier extends LogClassifier {

    @Override
    public LogType classify(String line) {
        if (line == null || line.isBlank()) {
            return LogType.UNKNOWN;
        }
        Map<String, String> fields = KeyValueLineParser.parse(line);
        if (isRequest(fields)) {
            return LogType.REQUEST;
        }
        if (isApm(fields)) {
            return LogType.APM;
        }
        if (isApplication(fields)) {
            return LogType.APPLICATION;
        }
        return LogType.UNKNOWN;
    }

    private static boolean isRequest(Map<String, String> fields) {
        return fields.containsKey("request_method")
                && fields.containsKey("request_url")
                && fields.containsKey("response_status")
                && fields.containsKey("response_time_ms");
    }

    private static boolean isApm(Map<String, String> fields) {
        return fields.containsKey("metric") && fields.containsKey("value");
    }

    private static boolean isApplication(Map<String, String> fields) {
        return fields.containsKey("level");
    }
}
