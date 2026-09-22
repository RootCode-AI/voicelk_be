package com.voicelk.voicelk_be.llm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class PlainTextFormatterTest {

    @Test
    void removesBoldAndItalicMarkers() {
        assertEquals("Photosynthesis යනු ශාක ආහාර නිපදවන ක්‍රියාවලියයි.",
                PlainTextFormatter.toPlainText("**Photosynthesis** යනු *ශාක* ආහාර නිපදවන __ක්‍රියාවලියයි__."));
    }

    @Test
    void turnsHeadingsAndListsIntoSentences() {
        String markdown = """
                ## Photosynthesis
                * **Chlorophyll** මගින් ආලෝකය අවශෝෂණය කරයි
                - CO2 සහ H2O භාවිතා කරයි
                1. Glucose නිපදවයි.
                """;
        assertEquals("Photosynthesis. Chlorophyll මගින් ආලෝකය අවශෝෂණය කරයි. CO2 සහ H2O භාවිතා කරයි. 1. Glucose නිපදවයි.",
                PlainTextFormatter.toPlainText(markdown));
    }

    @Test
    void keepsLinkTextAndCodeContent() {
        assertEquals("බලන්න Wikipedia සහ x = 5 ලෙස ගන්න.",
                PlainTextFormatter.toPlainText("බලන්න [Wikipedia](https://wikipedia.org) සහ `x = 5` ලෙස ගන්න."));
    }

    @Test
    void leavesUnderscoresInsideWordsAlone() {
        assertEquals("snake_case variable.", PlainTextFormatter.toPlainText("snake_case variable."));
    }

    @Test
    void passesThroughNullAndBlank() {
        assertNull(PlainTextFormatter.toPlainText(null));
        assertEquals("  ", PlainTextFormatter.toPlainText("  "));
    }
}
