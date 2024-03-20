package com.koroliuk.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java -cp src/main/java com.koroliuk.app.Main <inputFile> [--out outputFile] [--format= html|ansi]");
            System.exit(1);
        }
        String inputFilePath = args[0];
        String outputFilePath = null;
        String format = null;
        for (int i = 1; i < args.length; i++) {
            if ("--out".equals(args[i]) && i + 1 < args.length) {
                i++;
                outputFilePath = args[i];
            } else if (args[i].startsWith("--format=")) {
                format = args[i].substring(args[i].indexOf('=') + 1);
                if (!"html".equals(format) && !"ansi".equals(format)) {
                    System.err.println("Error: Invalid format. Use 'html' or 'ansi'.");
                    System.exit(1);
                }
            }
        }
        try {
            String markdownContent = Files.readString(Paths.get(inputFilePath));
            String convertedContent = getString(markdownContent, format, outputFilePath);
            if (outputFilePath != null) {
                Files.writeString(Paths.get(outputFilePath), convertedContent);
                System.out.println("Output written to " + outputFilePath);
            } else {
                System.out.println(convertedContent);
            }
        } catch (IOException e) {
            System.err.println("Error handling file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static String getString(String markdownContent, String format, String outputFilePath) throws Exception {
        Converter converter = new Converter(markdownContent);
        String convertedContent;
        if (format == null && outputFilePath != null) {
            convertedContent = converter.markdownConverter(Format.HTML);
        } else if (format == null || "ansi".equals(format)) {
            convertedContent = converter.markdownConverter(Format.ANSI);
        } else {
            convertedContent = converter.markdownConverter(Format.HTML);
        }
        return convertedContent;
    }
}