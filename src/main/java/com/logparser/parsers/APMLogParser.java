package com.logparser.parsers;

import com.logparser.models.APMModel;
import com.logparser.util.KeyValueLineParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class APMLogParser implements ILogParser {

    private final List<APMModel> entries = new ArrayList<>();

    @Override
    public boolean parse(String line) {
        Map<String, String> fields = KeyValueLineParser.parse(line);
        if (!fields.containsKey("metric") || !fields.containsKey("value")) {
            return false;
        }
        String metric = fields.get("metric");
        String raw = fields.get("value");
        if (metric == null || metric.isBlank()) {
            return false;
        }
        double value;
        try {
            value = Double.parseDouble(raw);
        } catch (NumberFormatException ex) {
            return false;
        }
        String timestamp = fields.getOrDefault("timestamp", "");
        entries.add(new APMModel(timestamp, metric, value));
        return true;
    }

    @Override
    public Map<String, Object> aggregate() {
        Map<String, List<Double>> valuesByMetric = new TreeMap<>();
        for (APMModel m : entries) {
            valuesByMetric.computeIfAbsent(m.metric(), k -> new ArrayList<>()).add(m.value());
        }
        Map<String, Object> out = new LinkedHashMap<>();
        for (Map.Entry<String, List<Double>> e : valuesByMetric.entrySet()) {
            List<Double> values = new ArrayList<>(e.getValue());
            Collections.sort(values);
            int n = values.size();
            double min = values.get(0);
            double max = values.get(n - 1);
            double sum = 0;
            for (double v : values) {
                sum += v;
            }
            double average = sum / n;
            double median = median(values);
            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("minimum", roundIfWhole(min));
            stats.put("median", roundIfWhole(median));
            stats.put("average", roundIfWhole(average));
            stats.put("max", roundIfWhole(max));
            out.put(e.getKey(), stats);
        }
        return out;
    }

    private static double median(List<Double> sorted) {
        int n = sorted.size();
        if (n % 2 == 1) {
            return sorted.get(n / 2);
        }
        return (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
    }

    /**
     * Emits integers when values are mathematically whole for cleaner JSON (matches sample style).
     */
    private static Number roundIfWhole(double v) {
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            return v;
        }
        long asLong = Math.round(v);
        if (Math.abs(v - asLong) < 1e-9) {
            return asLong;
        }
        return v;
    }
}
