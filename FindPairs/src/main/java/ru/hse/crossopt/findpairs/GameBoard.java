package ru.hse.crossopt.findpairs;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import static com.google.common.base.Preconditions.checkArgument;

/** Class that stores basic information about the state of the game. */
public class GameBoard {
    public static final int LARGEST_BOARD_SIZE = 10;
    @NotNull private final Card[][] cards;
    private final int size;

    /**
     * Generates a GameBoard with the given size.
     * It will have size * size cards with numbers from 0 to (size * size) / 2 - 1 on them.
     * Each number is written on exactly two cards.
     * @param size the size of the board, an even integer greater than 0 and not greater than LARGEST_BOARD_SIZE.
     */
    public GameBoard(int size) {
        checkArgument(size > 0 && size % 2 == 0 && size <= LARGEST_BOARD_SIZE);
        cards = new Card[size][size];
        this.size = size;
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int number = 0; 2 * number < size * size; ++number) {
            numbers.add(number);
            numbers.add(number);
        }
        Collections.shuffle(numbers);
        int currentNumber = 0;
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y < size; ++y) {
                cards[x][y] = new Card(numbers.get(currentNumber++));
            }
        }
    }

    /** Returns true if the game is finished, false otherwise. */
    public boolean isWin() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (!cards[i][j].wasFound()) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Returns the card at position (x, y) in the board. */
    public @NotNull Card get(int x, int y) {
        return cards[x][y];
    }
}
