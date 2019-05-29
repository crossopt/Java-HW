package ru.hse.crossopt.findpairs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest {
    private GameBoard board;

    @BeforeEach
    void setUp() {
        board = new GameBoard(6);
    }

    @RepeatedTest(3)
    void boardGenerationIsCorrect() {
        int cardsAmount = 18;
        int[] neededNumbers = new int[cardsAmount];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                neededNumbers[board.get(i, j).getNumber()]++;
            }
        }
        for (int i = 0; i < cardsAmount; i++) {
            assertEquals(2, neededNumbers[i]);
        }
    }

    @Test
    void badBoardSizeFails() {
        assertThrows(IllegalArgumentException.class, () -> new GameBoard(0));
        assertThrows(IllegalArgumentException.class, () -> new GameBoard(-5));
        assertThrows(IllegalArgumentException.class, () -> new GameBoard(7));
        assertThrows(IllegalArgumentException.class, () -> new GameBoard(100000));
    }

    @Test
    void isWinForNonWinningBoard() {
        assertFalse(board.isWin());
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (i + j > 0) {
                    board.get(i, j).setFound();
                    assertFalse(board.isWin());
                }
            }
        }
    }

    @Test
    void isWinForWinningBoard() {
        assertFalse(board.isWin());
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                board.get(i, j).setFound();
            }
        }
        assertTrue(board.isWin());
        assertTrue(board.isWin());
    }
}