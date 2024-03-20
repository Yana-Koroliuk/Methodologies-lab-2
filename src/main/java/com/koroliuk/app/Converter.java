package com.koroliuk.app;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Converter {
    String markdown;

    public Converter(String markdown) {
        this.markdown = markdown;
    }

    public String markdownConverter(Format format) throws Exception {
        List<String> preBlocks = new ArrayList<>();
        String converted = markdown;

        Pattern prePattern = Pattern.compile("(?m)(^\\n?|^)```(.*?)```(\\n?|$)", Pattern.DOTALL);
        Matcher preMatcher = prePattern.matcher(converted);
        StringBuilder stringBuilder = new StringBuilder();
        int preIndex = 0;
        while (preMatcher.find()) {
            String prefix = preMatcher.group(1);
            String suffix = preMatcher.group(3);
            preBlocks.add(preMatcher.group(2));
            String replacement = (prefix.isEmpty() ? "" : "\n") + "PREBLOCK" + preIndex++ + (suffix.isEmpty() ? "" : "\n");
            preMatcher.appendReplacement(stringBuilder, Matcher.quoteReplacement(replacement));
        }
        preMatcher.appendTail(stringBuilder);
        converted = stringBuilder.toString();
        String checkCopy = converted;

        String regexBold = "(?<![\\w`*\u0400-\u04FF])\\*\\*(\\S(?:.*?\\S)?)\\*\\*(?![\\w`*\u0400-\u04FF])";
        String regexItalic = "(?<![\\w`*\\u0400-\\u04FF])_(\\S(?:.*?\\S)?)_(?![\\w`*\\u0400-\\u04FF])";
        String regexMonospaced = "(?<![\\w`*\\u0400-\\u04FF])`(\\S(?:.*?\\S)?)`(?![\\w`*\\u0400-\\u04FF])";

        List<String> boldBlocks = getMatchPatternList(regexBold, converted);
        List<String> italicBlocks = getMatchPatternList(regexItalic, converted);
        List<String> monospacedBlocks = getMatchPatternList(regexMonospaced, converted);
        checkNestedMarkers(regexItalic, regexMonospaced, boldBlocks);
        checkNestedMarkers(regexBold, regexItalic, monospacedBlocks);
        checkNestedMarkers(regexBold, regexMonospaced, italicBlocks);

        if (format.equals(Format.HTML)) {
            converted = converted.replaceAll(regexBold, "<b>$1</b>");
            converted = converted.replaceAll(regexItalic, "<i>$1</i>");
            converted = converted.replaceAll(regexMonospaced, "<tt>$1</tt>");
            String[] paragraphs = converted.split("\n{2,}");
            StringBuilder htmlBuilder = new StringBuilder();
            for (String paragraph : paragraphs) {
                if (!paragraph.isEmpty()) {
                    htmlBuilder.append("<p>").append(paragraph).append("</p>\n");
                }
            }
        converted = htmlBuilder.toString();
        } else {
            converted = converted.replaceAll(regexBold, "\u001B[1m$1\u001B[22m");
            converted = converted.replaceAll(regexItalic, "\u001B[3m$1\u001B[23m");
            converted = converted.replaceAll(regexMonospaced, "\u001B[7m$1\u001B[27m");
        }
        checkCopy = checkCopy.replaceAll(regexBold, "boldBlock");
        checkCopy = checkCopy.replaceAll(regexItalic, "italicBlock");
        checkCopy = checkCopy.replaceAll(regexMonospaced, "monospacedBlock");
        for (int i = 0; i < preBlocks.size(); i++) {
            converted = format.equals(Format.HTML) ? converted.replace("PREBLOCK" + i, "<pre>" + preBlocks.get(i) + "</pre>") :
            converted.replace("PREBLOCK" + i, "\u001B[7m" + preBlocks.get(i) + "\u001B[27m");
        }
        checkForUnbalancedMarkers(checkCopy);
        return converted;
    }

    private void checkForUnbalancedMarkers(String checkCopy) throws Exception {
        if (hasUnbalancedMarkers(checkCopy, "**") ||
                hasUnbalancedMarkers(checkCopy, "_") ||
                hasUnbalancedMarkers(checkCopy, "`") ||
                hasUnbalancedMarkers(checkCopy, "```")) {
            throw new Exception("ERROR: There is a start but no end among the markup elements");
        }
    }

    private boolean hasUnbalancedMarkers(String text, String marker) {
        int openPos = -1;

        for (int idx = 0; idx < text.length(); idx++) {
            if (text.startsWith(marker, idx)) {
                boolean atWordEnd = (idx > 0 && Character.isLetterOrDigit(text.charAt(idx - 1))) &&
                        (idx + marker.length() == text.length() || !Character.isLetterOrDigit(text.charAt(idx + marker.length())));
                String regex = "[A-Za-z0-9,\\u0400-\\u04FF]";
                boolean beforeIsMatch = idx > 0 && Character.toString(text.charAt(idx - 1)).matches(regex);
                boolean afterIsMatch = idx + marker.length() < text.length() && Character.toString(text.charAt(idx + marker.length())).matches(regex);

                if (atWordEnd || ((!beforeIsMatch && !afterIsMatch) || (beforeIsMatch && afterIsMatch))) {
                    idx += marker.length() - 1;
                    continue;
                }
                if (openPos == -1) {
                    openPos = idx;
                    idx += marker.length() - 1;
                }
            }
            if (openPos != -1 && (text.charAt(idx) == '\n' || idx == text.length() - 1)) {
                return true;
            }
        }

        return openPos != -1;
    }


    private List<String> getMatchPatternList(String regex, String html) {
        List<String> regexBlocks = new ArrayList<>();
        Pattern regexPatten = Pattern.compile(regex, Pattern.DOTALL);
        Matcher regexMatcher = regexPatten.matcher(html);
        while (regexMatcher.find()) {
            regexBlocks.add(regexMatcher.group(1));
        }
        return regexBlocks;
    }

    private void checkNestedMarkers(String regex1, String regex2, List<String> blocks) throws Exception {
        Pattern regex1Pattern = Pattern.compile(regex1, Pattern.DOTALL);
        Pattern regex2Pattern = Pattern.compile(regex2, Pattern.DOTALL);
        for (String block : blocks) {
            Matcher regex1Matcher = regex1Pattern.matcher(block);
            Matcher regex2Matcher = regex2Pattern.matcher(block);
            boolean matcher1 = regex1Matcher.find();
            boolean matcher2 = regex2Matcher.find();
            if (matcher1 | matcher2) {
                throw new Exception("ERROR: There is nested markers");
            }
        }
    }
}
