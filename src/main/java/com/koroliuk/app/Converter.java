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

    public String markdownToHtml() throws Exception {
        List<String> preBlocks = new ArrayList<>();
        String html = markdown;

        Pattern prePattern = Pattern.compile("(?m)(^\\n?|^)```(.*?)```(\\n?|$)", Pattern.DOTALL);
        Matcher preMatcher = prePattern.matcher(html);
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
        html = stringBuilder.toString();
        String checkCopy = html;

        String regexBold = "(?<![\\w`*\u0400-\u04FF])\\*\\*(\\S(?:.*?\\S)?)\\*\\*(?![\\w`*\u0400-\u04FF])";
        String regexItalic = "(?<![\\w`*\\u0400-\\u04FF])_(\\S(?:.*?\\S)?)_(?![\\w`*\\u0400-\\u04FF])";
        String regexMonospaced = "(?<![\\w`*\\u0400-\\u04FF])`(\\S(?:.*?\\S)?)`(?![\\w`*\\u0400-\\u04FF])";

        List<String> boldBlocks = getMatchPatternList(regexBold, html);
        List<String> italicBlocks = getMatchPatternList(regexItalic, html);
        List<String> monospacedBlocks = getMatchPatternList(regexMonospaced, html);
        checkNestedMarkers(regexItalic, regexMonospaced, boldBlocks);
        checkNestedMarkers(regexBold, regexItalic, monospacedBlocks);
        checkNestedMarkers(regexBold, regexMonospaced, italicBlocks);

        html = html.replaceAll(regexBold, "<b>$1</b>");
        checkCopy = checkCopy.replaceAll(regexBold, "boldBlock");
        html = html.replaceAll(regexItalic, "<i>$1</i>");
        checkCopy = checkCopy.replaceAll(regexItalic, "italicBlock");
        html = html.replaceAll(regexMonospaced, "<tt>$1</tt>");
        checkCopy = checkCopy.replaceAll(regexMonospaced, "monospacedBlock");

        String[] paragraphs = html.split("\n{2,}");
        StringBuilder htmlBuilder = new StringBuilder();
        for (String paragraph : paragraphs) {
            if (!paragraph.isEmpty()) {
                htmlBuilder.append("<p>").append(paragraph).append("</p>\n");
            }
        }
        html = htmlBuilder.toString();
        for (int i = 0; i < preBlocks.size(); i++) {
            html = html.replace("PREBLOCK" + i, "<pre>" + preBlocks.get(i) + "</pre>");
        }
        checkForUnbalancedMarkers(checkCopy);
        return html;
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
        int closePos = -1;

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
                if (openPos == -1 || closePos != -1) {
                    openPos = idx;
                    closePos = -1;
                    idx += marker.length() - 1;
                } else if (openPos != -1) {
                    closePos = idx;
                    idx += marker.length() - 1;
                }
            }
            if (openPos != -1 && closePos == -1 && (text.charAt(idx) == '\n' || idx == text.length() - 1)) {
                return true;
            }
        }

        return openPos != -1 && closePos == -1;
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
