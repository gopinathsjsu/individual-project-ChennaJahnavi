package com.logparser.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Parses space-delimited {@code key=value} segments. Values may be double-quoted (with optional {@code \} escapes).
 */
public final class KeyValueLineParser {

    private KeyValueLineParser() {
    }

    public static Map<String, String> parse(String line) {
        Map<String, String> map = new LinkedHashMap<>();
        if (line == null || line.isBlank()) {
            return map;
        }
        int i = 0;
        int n = line.length();
        while (i < n) {
            while (i < n && Character.isWhitespace(line.charAt(i))) {
                i++;
            }
            if (i >= n) {
                break;
            }
            int keyStart = i;
            while (i < n && line.charAt(i) != '=') {
                i++;
            }
            if (i >= n) {
                break;
            }
            String key = line.substring(keyStart, i).trim();
            i++;
            if (i >= n) {
                map.put(key, "");
                break;
            }
            String value;
            if (line.charAt(i) == '"') {
                i++;
                StringBuilder sb = new StringBuilder();
                while (i < n) {
                    char c = line.charAt(i);
                    if (c == '\\' && i + 1 < n) {
                        sb.append(line.charAt(i + 1));
                        i += 2;
                        continue;
                    }
                    if (c == '"') {
                        i++;
                        break;
                    }
                    sb.append(c);
                    i++;
                }
                value = sb.toString();
            } else {
                int valStart = i;
                while (i < n && !Character.isWhitespace(line.charAt(i))) {
                    i++;
                }
                value = line.substring(valStart, i);
            }
            map.put(key, value);
        }
        return map;
    }
}
