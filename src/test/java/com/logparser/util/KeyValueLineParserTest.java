package com.logparser.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyValueLineParserTest {

    @Test
    void parsesUnquotedAndQuotedValues() {
        String line =
                "timestamp=2024-02-24T16:22:20Z level=INFO message=\"Scheduled maintenance starting\" host=webserver1";
        Map<String, String> m = KeyValueLineParser.parse(line);
        assertEquals("2024-02-24T16:22:20Z", m.get("timestamp"));
        assertEquals("INFO", m.get("level"));
        assertEquals("Scheduled maintenance starting", m.get("message"));
        assertEquals("webserver1", m.get("host"));
    }

    @Test
    void parsesEscapedQuoteInsideMessage() {
        String line = "k=\"a \\\"b\\\" c\" x=1";
        Map<String, String> m = KeyValueLineParser.parse(line);
        assertEquals("a \"b\" c", m.get("k"));
        assertEquals("1", m.get("x"));
    }

    @Test
    void blankLineYieldsEmptyMap() {
        assertTrue(KeyValueLineParser.parse("   ").isEmpty());
    }
}
