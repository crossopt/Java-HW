package ru.hse.crossopt.findpairs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {
    @Test
    void cardGetNumberWorks() {
        Card card = new Card(5);
        assertEquals(5, card.getNumber());
    }

    @Test
    void cardBadNumber() {
       assertThrows(IllegalArgumentException.class, () -> new Card(-5));
    }

    @Test
    void wasFoundIsFalse() {
        Card card = new Card(5);
        assertFalse(card.wasFound());
    }

    @Test
    void wasFoundAfterSetFoundIsTrue() {
        Card card = new Card(5);
        card.setFound();
        assertTrue(card.wasFound());
        card.setFound();
        assertTrue(card.wasFound());
    }
}