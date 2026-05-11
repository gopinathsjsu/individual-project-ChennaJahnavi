package com.logparser;

import java.nio.file.Files;
import java.nio.file.Path;

final class CliArgs {

    private CliArgs() {
    }

    static Path parseInputFile(String[] args) {
        if (args == null || args.length != 2 || !"--file".equals(args[0]) || args[1].isBlank()) {
            throw new IllegalArgumentException("Usage: java -jar log-parser.jar --file <filename.txt>");
        }
        Path p = Path.of(args[1]);
        if (!Files.isRegularFile(p)) {
            throw new IllegalArgumentException("Input file not found: " + p);
        }
        String name = p.getFileName().toString();
        if (!name.endsWith(".txt")) {
            throw new IllegalArgumentException("Input file must have .txt extension: " + p);
        }
        return p;
    }
}
