package com.logparser.parsers;

import com.logparser.models.AppLogModel;
import com.logparser.util.KeyValueLineParser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ApplicationLogParser implements ILogParser {

    private final List<AppLogModel> entries = new ArrayList<>();

    @Override
    public boolean parse(String line) {
        Map<String, String> fields = KeyValueLineParser.parse(line);
        if (!fields.containsKey("level")) {
            return false;
        }
        String level = fields.get("level");
        if (level == null || level.isBlank()) {
            return false;
        }
        String timestamp = fields.getOrDefault("timestamp", "");
        String message = fields.getOrDefault("message", "");
        entries.add(new AppLogModel(timestamp, level.toUpperCase(), message));
        return true;
    }

    @Override
    public Map<String, Object> aggregate() {
        Map<String, Integer> counts = new TreeMap<>();
        for (AppLogModel e : entries) {
            counts.merge(e.level(), 1, Integer::sum);
        }
        return new LinkedHashMap<>(counts);
    }
}
