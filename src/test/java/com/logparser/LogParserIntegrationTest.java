package com.logparser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogParserIntegrationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void writesThreeJsonFilesEvenWhenEmptyInput(@TempDir Path dir) throws Exception {
        Path input = dir.resolve("empty.txt");
        Files.writeString(input, "\n# ignore\nnot=key value format\n");
        Path out = dir.resolve("out");
        Files.createDirectories(out);
        LogParser.createDefault().run(input, out);
        assertTrue(Files.isRegularFile(out.resolve("apm.json")));
        assertTrue(Files.isRegularFile(out.resolve("application.json")));
        assertTrue(Files.isRegularFile(out.resolve("request.json")));
        assertEquals("{}", Files.readString(out.resolve("apm.json")).replaceAll("\\s+", ""));
    }

    @Test
    void sampleFileProducesExpectedShapes(@TempDir Path dir) throws Exception {
        Path input = Paths.get(Objects.requireNonNull(
                        getClass().getResource("/sample_logs/project_sample.txt"))
                .toURI());
        Path out = dir.resolve("out");
        LogParser.createDefault().run(input, out);

        JsonNode apm = mapper.readTree(out.resolve("apm.json").toFile());
        assertTrue(apm.has("cpu_usage_percent"));
        assertTrue(apm.get("cpu_usage_percent").has("median"));

        JsonNode app = mapper.readTree(out.resolve("application.json").toFile());
        assertEquals(2, app.get("ERROR").asInt());

        JsonNode req = mapper.readTree(out.resolve("request.json").toFile());
        assertTrue(req.has("/api/status"));
        assertEquals(3, req.get("/api/status").get("status_codes").get("2XX").asInt());
    }
}
