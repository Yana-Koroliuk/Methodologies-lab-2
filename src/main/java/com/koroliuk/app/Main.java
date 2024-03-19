package com.koroliuk.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java -cp src/main/java com.koroliuk.app.Main <inputFile> [--out outputFile]");
            System.exit(1);
        }
        String inputFilePath = args[0];
        String outputFilePath = null;
        if (args.length > 2 && "--out".equals(args[1])) {
            outputFilePath = args[2];
        }
        try {
            String markdownContent = Files.readString(Paths.get(inputFilePath));
            Converter converter = new Converter(markdownContent);
            String htmlContent = converter.markdownToHtml();
            if (outputFilePath != null) {
                Files.writeString(Paths.get(outputFilePath), htmlContent);
                System.out.println("Output written to " + outputFilePath);
            } else {
                System.out.println(htmlContent);
            }
        } catch (IOException e) {
            System.err.println("Error handling file: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}