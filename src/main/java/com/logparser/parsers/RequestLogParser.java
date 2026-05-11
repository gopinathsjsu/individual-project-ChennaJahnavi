package com.logparser.parsers;

import com.logparser.models.RequestModel;
import com.logparser.util.KeyValueLineParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RequestLogParser implements ILogParser {

    private final List<RequestModel> entries = new ArrayList<>();

    @Override
    public boolean parse(String line) {
        Map<String, String> fields = KeyValueLineParser.parse(line);
        if (!fields.containsKey("request_method")
                || !fields.containsKey("request_url")
                || !fields.containsKey("response_status")
                || !fields.containsKey("response_time_ms")) {
            return false;
        }
        String method = fields.get("request_method");
        String url = stripQuotes(fields.get("request_url"));
        if (method == null || method.isBlank() || url == null || url.isBlank()) {
            return false;
        }
        int status;
        int timeMs;
        try {
            status = Integer.parseInt(fields.get("response_status"));
            timeMs = Integer.parseInt(fields.get("response_time_ms"));
        } catch (NumberFormatException ex) {
            return false;
        }
        String timestamp = fields.getOrDefault("timestamp", "");
        entries.add(new RequestModel(timestamp, method, url, status, timeMs));
        return true;
    }

    private static String stripQuotes(String raw) {
        if (raw == null) {
            return null;
        }
        if (raw.length() >= 2 && raw.startsWith("\"") && raw.endsWith("\"")) {
            return raw.substring(1, raw.length() - 1);
        }
        return raw;
    }

    @Override
    public Map<String, Object> aggregate() {
        Map<String, List<RequestModel>> byRoute = new TreeMap<>();
        for (RequestModel r : entries) {
            byRoute.computeIfAbsent(r.url(), k -> new ArrayList<>()).add(r);
        }
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<String, List<RequestModel>> e : byRoute.entrySet()) {
            out.put(e.getKey(), summarizeRoute(e.getValue()));
        }
        return out;
    }

    private static Map<String, Object> summarizeRoute(List<RequestModel> rows) {
        List<Integer> times = new ArrayList<>();
        int c2 = 0;
        int c4 = 0;
        int c5 = 0;
        for (RequestModel r : rows) {
            times.add(r.timeMs());
            int bucket = r.status() / 100;
            if (bucket == 2) {
                c2++;
            } else if (bucket == 4) {
                c4++;
            } else if (bucket == 5) {
                c5++;
            }
        }
        Collections.sort(times);
        int n = times.size();
        int min = times.get(0);
        int max = times.get(n - 1);
        int p95 = percentileNearestRank95(times);

        Map<String, Object> responseTimes = new LinkedHashMap<>();
        responseTimes.put("min", min);
        responseTimes.put("95_percentile", p95);
        responseTimes.put("max", max);

        Map<String, Object> statusCodes = new LinkedHashMap<>();
        statusCodes.put("2XX", c2);
        statusCodes.put("4XX", c4);
        statusCodes.put("5XX", c5);

        Map<String, Object> route = new LinkedHashMap<>();
        route.put("response_times", responseTimes);
        route.put("status_codes", statusCodes);
        return route;
    }

    /**
     * Nearest-rank 95th percentile (1-based rank r = ceil(0.95 * N)), on sorted samples.
     */
    static int percentileNearestRank95(List<Integer> sortedAscending) {
        int n = sortedAscending.size();
        if (n == 0) {
            throw new IllegalArgumentException("empty");
        }
        int rank = (int) Math.ceil(0.95 * n);
        rank = Math.min(Math.max(rank, 1), n);
        return sortedAscending.get(rank - 1);
    }
}
