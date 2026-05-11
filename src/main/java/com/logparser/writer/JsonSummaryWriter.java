package com.logparser.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class JsonSummaryWriter {

    private final ObjectMapper mapper;

    public JsonSummaryWriter() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void write(Path directory, String fileName, Map<String, Object> payload) throws IOException {
        Path target = directory.resolve(fileName);
        mapper.writeValue(target.toFile(), payload == null ? Map.of() : payload);
    }

    public void writeAll(Path outputDirectory, Map<String, Object> apm, Map<String, Object> application, Map<String, Object> request)
            throws IOException {
        Files.createDirectories(outputDirectory);
        write(outputDirectory, "apm.json", apm);
        write(outputDirectory, "application.json", application);
        write(outputDirectory, "request.json", request);
    }
}
