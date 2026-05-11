package com.logparser;

import com.logparser.classifier.DefaultLogClassifier;
import com.logparser.classifier.LogClassifier;
import com.logparser.classifier.LogType;
import com.logparser.factory.ParserFactory;
import com.logparser.parsers.ILogParser;
import com.logparser.writer.JsonSummaryWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Orchestrates read → classify → parse → aggregate → write.
 */
public class LogParser {

    private final LogClassifier classifier;
    private final ParserFactory parserFactory;
    private final JsonSummaryWriter writer;

    public LogParser(LogClassifier classifier, ParserFactory parserFactory, JsonSummaryWriter writer) {
        this.classifier = classifier;
        this.parserFactory = parserFactory;
        this.writer = writer;
    }

    public static LogParser createDefault() {
        return new LogParser(new DefaultLogClassifier(), new ParserFactory(), new JsonSummaryWriter());
    }

    public void run(Path inputFile, Path outputDirectory) throws IOException {
        Map<LogType, ILogParser> parsers = parserFactory.createParsersForRun();
        List<String> lines = Files.readAllLines(inputFile, StandardCharsets.UTF_8);
        for (String line : lines) {
            LogType type = classifier.classify(line);
            if (type == LogType.UNKNOWN) {
                continue;
            }
            ILogParser parser = parserFactory.create(type, parsers);
            parser.parse(line);
        }
        ILogParser apm = parsers.get(LogType.APM);
        ILogParser app = parsers.get(LogType.APPLICATION);
        ILogParser req = parsers.get(LogType.REQUEST);
        writer.writeAll(outputDirectory, apm.aggregate(), app.aggregate(), req.aggregate());
    }
}
