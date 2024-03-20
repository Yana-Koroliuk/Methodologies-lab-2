package com.koroliuk.app;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;

class MainTest {
    private Path inputFilePath;
    private Path outputFilePath;

    @BeforeEach
    void setUp() throws IOException {
        inputFilePath = Files.createTempFile("testInput", ".md");
        Files.writeString(inputFilePath, "Test Markdown");
        outputFilePath = Path.of(inputFilePath.getParent().toString(), "testOutput.html");
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(inputFilePath);
        Files.deleteIfExists(outputFilePath);
    }

    @Test
    void testOutputFileCreation() throws Exception {
        Main.main(new String[]{inputFilePath.toString(), "--out", outputFilePath.toString(), "--format=html"});
        assertTrue(Files.exists(outputFilePath));
        String expectedOutput = "<p>Test Markdown</p>";
        String actualOutput = Files.readString(outputFilePath);
        assertEquals(expectedOutput, actualOutput.trim());
    }

    @Test
    void testFormatNullAndOutputFileCreation() throws Exception {
        Main.main(new String[]{inputFilePath.toString(), "--out", outputFilePath.toString()});
        assertTrue(Files.exists(outputFilePath));
        String expectedOutput = "<p>Test Markdown</p>";
        String actualOutput = Files.readString(outputFilePath);
        assertEquals(expectedOutput, actualOutput.trim());
    }

    @Test
    void testStandardOutput() {
        final PrintStream originalOut = System.out;
        final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        Main.main(new String[]{inputFilePath.toString()});
        System.setOut(originalOut);
        String expectedOutputPart = "Test Markdown";
        assertTrue(outContent.toString().contains(expectedOutputPart));
    }
}
