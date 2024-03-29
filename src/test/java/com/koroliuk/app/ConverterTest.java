package com.koroliuk.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ConverterTest {

    @Test
    void TestMarkdownToHtmlBoldText() throws Exception {
        String arrangeMarkdown = "**bold**";
        String expectedHtml = """
                <p><b>bold</b></p>
                """;
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedHtml, converter.markdownConverter(Format.HTML));
    }

    @Test
    void TestMarkdownToHtmlItalicText() throws Exception {
        String arrangeMarkdown = "_italic_";
        String expectedHtml = """
                <p><i>italic</i></p>
                """;
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedHtml, converter.markdownConverter(Format.HTML));
    }

    @Test
    void TestMarkdownToHtmlMonospacedText() throws Exception {
        String arrangeMarkdown = "`monospaced`";
        String expectedHtml = """
                <p><tt>monospaced</tt></p>
                """;
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedHtml, converter.markdownConverter(Format.HTML));
    }

    @Test
    void TestMarkdownToHtmlPreformattedText() throws Exception {
        String arrangeMarkdown = """
                ```
                Preformatted text **preformatted**
                ```
                """;
        String expectedHtml = """
                <p><pre>
                Preformatted text **preformatted**
                </pre>
                </p>
                """;
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedHtml, converter.markdownConverter(Format.HTML));
    }

    @Test
    void TestMarkdownToHtmlParagraphText() throws Exception {
        String arrangeMarkdown = """
                Paragraph1. Some example of the text.
                This is still paragraph 1.
                               
                And after a blank line this is paragraph 2.""";
        String expectedHtml = """
                <p>Paragraph1. Some example of the text.
                This is still paragraph 1.</p>
                <p>And after a blank line this is paragraph 2.</p>
                """;
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedHtml, converter.markdownConverter(Format.HTML));
    }

    @ParameterizedTest
    @ValueSource(strings = {"**`_not valid_`**", "_`not valid`_", "`**not valid**`", "_**not valid**_"})
    void testMarkdownToHtmlNestedMarkers(String input) {
        Converter converter = new Converter(input);
        Exception exception = assertThrows(Exception.class, () -> converter.markdownConverter(Format.HTML));
        assertTrue(exception.getMessage().contains("ERROR: There is nested markers"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"_start-but-not end", "_start-but-not\nend-in-the-same-line_", "**written **text"})
    void testMarkdownToHtmlUnbalancedMarkers(String input) {
        Converter converter = new Converter(input);
        Exception exception = assertThrows(Exception.class, () -> converter.markdownConverter(Format.HTML));
        assertTrue(exception.getMessage().contains("ERROR: There is a start but no end among the markup elements"));
    }

    @Test
    void TestMarkdownToHtmlWhenMarkerDividedFromText() throws Exception {
        String arrangeMarkdown = """
                ** - відірвана від тексту розмітка""";
        String expectedHtml = """
                <p>** - відірвана від тексту розмітка</p>
                """;
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedHtml, converter.markdownConverter(Format.HTML));
    }

    @Test
    void TestMarkdownToHtmlWhenMarkerDividedFromTextAndNested() throws Exception {
        String arrangeMarkdown = """
                `_` - відірвана від тексту розмітка""";
        String expectedHtml = """
                <p><tt>_</tt> - відірвана від тексту розмітка</p>
                """;
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedHtml, converter.markdownConverter(Format.HTML));
    }

    @Test
    void TestMarkdownToHtmlWhenMarkerIsPartOfText() throws Exception {
        String arrangeMarkdown = """
                test_case_with_underscore""";
        String expectedHtml = """
                <p>test_case_with_underscore</p>
                """;
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedHtml, converter.markdownConverter(Format.HTML));
    }

    @Test
    void TestMarkdownToHtmlWhenMarkerInTheEndOfWord() throws Exception {
        String arrangeMarkdown = """
                some text**
                written more text""";
        String expectedHtml = """
                <p>some text**
                written more text</p>
                """;
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedHtml, converter.markdownConverter(Format.HTML));
    }

    @Test
    void TestMarkdownToHtmlWhenMarkerInTheEndOfText() throws Exception {
        String arrangeMarkdown = """
                some text written
                underscore_""";
        String expectedHtml = """
                <p>some text written
                underscore_</p>
                """;
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedHtml, converter.markdownConverter(Format.HTML));
    }

    @Test
    void TestMarkdownToAnsiBoldText() throws Exception {
        String arrangeMarkdown = "**bold**";
        String expectedAnsi = """
                \u001B[1mbold\u001B[22m""";
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedAnsi, converter.markdownConverter(Format.ANSI));
    }

    @Test
    void TestMarkdownToAnsiItalicText() throws Exception {
        String arrangeMarkdown = "_italic_";
        String expectedAnsi = """
                \u001B[3mitalic\u001B[23m""";
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedAnsi, converter.markdownConverter(Format.ANSI));
    }

    @Test
    void TestMarkdownToAnsiMonospacedText() throws Exception {
        String arrangeMarkdown = "`monospaced`";
        String expectedAnsi = """
                \u001B[7mmonospaced\u001B[27m""";
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedAnsi, converter.markdownConverter(Format.ANSI));
    }

    @Test
    void TestMarkdownToAnsiPreformattedText() throws Exception {
        String arrangeMarkdown = """
                ```
                Preformatted text **preformatted**
                ```
                """;
        String expectedAnsi = """
                \u001B[7m
                Preformatted text **preformatted**
                \u001B[27m
                """;
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedAnsi, converter.markdownConverter(Format.ANSI));
    }

    @Test
    void TestMarkdownToAnsiParagraphText() throws Exception {
        String arrangeMarkdown = """
                Paragraph1. Some example of the text.
                This is still paragraph 1.
                               
                And after a blank line this is paragraph 2.""";
        String expectedAnsi = """
                Paragraph1. Some example of the text.
                This is still paragraph 1.
                
                And after a blank line this is paragraph 2.""";
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedAnsi, converter.markdownConverter(Format.ANSI));
    }

    @Test
    void TestMarkdownToAnsiWhenMarkerDividedFromText() throws Exception {
        String arrangeMarkdown = """
                ** - відірвана від тексту розмітка""";
        String expectedAnsi = """
                ** - відірвана від тексту розмітка""";
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedAnsi, converter.markdownConverter(Format.ANSI));
    }

    @Test
    void TestMarkdownToAnsiWhenMarkerDividedFromTextAndNested() throws Exception {
        String arrangeMarkdown = """
                `_` - відірвана від тексту розмітка""";
        String expectedAnsi = """
                \u001B[7m_\u001B[27m - відірвана від тексту розмітка""";
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedAnsi, converter.markdownConverter(Format.ANSI));
    }

    @Test
    void TestMarkdownToAnsiWhenMarkerIsPartOfText() throws Exception {
        String arrangeMarkdown = """
                test_case_with_underscore""";
        String expectedAnsi = """
                test_case_with_underscore""";
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedAnsi, converter.markdownConverter(Format.ANSI));
    }

    @Test
    void TestMarkdownToAnsiWhenMarkerInTheEndOfWord() throws Exception {
        String arrangeMarkdown = """
                some text**
                written more text""";
        String expectedAnsi = """
                some text**
                written more text""";
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedAnsi, converter.markdownConverter(Format.ANSI));
    }

    @Test
    void TestMarkdownToAnsiWhenMarkerInTheEndOfText() throws Exception {
        String arrangeMarkdown = """
                some text written
                underscore_""";
        String expectedAnsi = """
                some text written
                underscore_""";
        Converter converter = new Converter(arrangeMarkdown);
        assertEquals(expectedAnsi, converter.markdownConverter(Format.ANSI));
    }
}