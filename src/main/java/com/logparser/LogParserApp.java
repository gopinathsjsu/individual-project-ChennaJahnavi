package com.logparser;

import java.nio.file.Path;

public final class LogParserApp {

    private LogParserApp() {
    }

    public static void main(String[] args) {
        try {
            Path input = CliArgs.parseInputFile(args);
            Path outputDir = Path.of("").toAbsolutePath().normalize();
            LogParser.createDefault().run(input, outputDir);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            System.exit(2);
        }
    }
}
