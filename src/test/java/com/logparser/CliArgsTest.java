package com.logparser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CliArgsTest {

    @Test
    void parsesFileFlag(@TempDir Path dir) throws Exception {
        Path f = dir.resolve("in.txt");
        Files.writeString(f, "x");
        Path p = CliArgs.parseInputFile(new String[] {"--file", f.toString()});
        assertEquals(f.toAbsolutePath().normalize(), p.toAbsolutePath().normalize());
    }

    @Test
    void rejectsMissingOrNonTxt(@TempDir Path dir) {
        assertThrows(IllegalArgumentException.class, () -> CliArgs.parseInputFile(new String[] {}));
        assertThrows(IllegalArgumentException.class, () -> CliArgs.parseInputFile(new String[] {"--file"}));
        assertThrows(IllegalArgumentException.class, () -> CliArgs.parseInputFile(new String[] {"--file", "nope.log"}));
        Path missing = dir.resolve("missing.txt");
        assertThrows(IllegalArgumentException.class, () -> CliArgs.parseInputFile(new String[] {"--file", missing.toString()}));
    }
}
