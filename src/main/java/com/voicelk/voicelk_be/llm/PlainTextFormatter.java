package com.voicelk.voicelk_be.llm;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Turns an LLM reply into plain prose that the speech model can read aloud.
 *
 * <p>Gemini answers in Markdown even when asked not to, and the TTS front-end would
 * otherwise try to voice the markup ("asterisk asterisk ..."). Formatting is removed
 * while the words are kept, and line-based structure (headings, list items) becomes
 * sentence boundaries so the voice still pauses where the layout did.
 */
public final class PlainTextFormatter {

    private static final Pattern CODE_FENCE = Pattern.compile("```[\\w-]*\\s*\\n?|```");
    private static final Pattern INLINE_CODE = Pattern.compile("`([^`]*)`");
    private static final Pattern IMAGE = Pattern.compile("!\\[([^\\]]*)]\\([^)]*\\)");
    private static final Pattern LINK = Pattern.compile("\\[([^\\]]+)]\\([^)]*\\)");
    private static final Pattern HTML_TAG = Pattern.compile("<[^>]+>");
    private static final Pattern BOLD = Pattern.compile("(\\*\\*|__)(.+?)\\1");
    private static final Pattern STRIKE = Pattern.compile("~~(.+?)~~");
    private static final Pattern ITALIC_STAR = Pattern.compile("(?<![\\w*])\\*(?!\\s)(.+?)(?<!\\s)\\*(?![\\w*])");
    private static final Pattern ITALIC_UNDERSCORE = Pattern.compile("(?<![\\p{L}\\p{N}_])_(?!\\s)(.+?)(?<!\\s)_(?![\\p{L}\\p{N}_])");

    private static final Pattern HEADING = Pattern.compile("^#{1,6}\\s*");
    private static final Pattern BLOCKQUOTE = Pattern.compile("^(>\\s?)+");
    private static final Pattern BULLET = Pattern.compile("^[-*+•]\\s+");
    private static final Pattern RULE = Pattern.compile("^([-*_=]\\s*){3,}$");
    private static final Pattern TABLE_DIVIDER = Pattern.compile("^\\|?\\s*:?-{2,}:?\\s*(\\|\\s*:?-{2,}:?\\s*)*\\|?$");

    private static final Pattern LEFTOVER_MARKS = Pattern.compile("[*#`~]");
    private static final Pattern SPACES = Pattern.compile("[ \\t\\u00A0]+");
    private static final Pattern SPACE_BEFORE_PUNCT = Pattern.compile("\\s+([.,;:!?])");

    private PlainTextFormatter() {
    }

    public static String toPlainText(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }

        String cleaned = text.replace("\r\n", "\n").replace('\r', '\n');
        cleaned = CODE_FENCE.matcher(cleaned).replaceAll("");
        cleaned = INLINE_CODE.matcher(cleaned).replaceAll("$1");
        cleaned = IMAGE.matcher(cleaned).replaceAll("$1");
        cleaned = LINK.matcher(cleaned).replaceAll("$1");
        cleaned = HTML_TAG.matcher(cleaned).replaceAll(" ");

        List<String> sentences = new ArrayList<>();
        for (String rawLine : cleaned.split("\n")) {
            String line = rawLine.strip();
            if (line.isEmpty() || RULE.matcher(line).matches() || TABLE_DIVIDER.matcher(line).matches()) {
                continue;
            }
            line = BLOCKQUOTE.matcher(line).replaceFirst("");
            line = HEADING.matcher(line).replaceFirst("");
            line = BULLET.matcher(line).replaceFirst("");
            line = stripInlineEmphasis(line);
            line = line.replace('|', ' ');
            line = LEFTOVER_MARKS.matcher(line).replaceAll("");
            line = SPACES.matcher(line).replaceAll(" ").strip();
            if (line.isEmpty()) {
                continue;
            }
            sentences.add(endsSentence(line) ? line : line + ".");
        }

        String joined = String.join(" ", sentences);
        return SPACE_BEFORE_PUNCT.matcher(joined).replaceAll("$1");
    }

    private static String stripInlineEmphasis(String line) {
        String result = BOLD.matcher(line).replaceAll("$2");
        result = STRIKE.matcher(result).replaceAll("$1");
        result = ITALIC_STAR.matcher(result).replaceAll("$1");
        return ITALIC_UNDERSCORE.matcher(result).replaceAll("$1");
    }

    private static boolean endsSentence(String line) {
        char last = line.charAt(line.length() - 1);
        return ".!?:;,।".indexOf(last) >= 0;
    }
}
