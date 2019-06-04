package ru.hse.crossopt.findpairs;

import javafx.scene.control.Button;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/** Class describing a card on the game board. */
public class Card {
    private final int number;
    private boolean wasFound;
    private @Nullable Button button;

    /** Creates a new card with the given number on it. */
    public Card(int number) {
        checkArgument(number >= 0);
        this.number = number;
        wasFound = false;
    }

    /** Returns the card's number. */
    public int getNumber() {
        return number;
    }

    /** Returns true if the card has been found, false otherwise. */
    public boolean wasFound() {
        return wasFound;
    }

    /** Sets the card's state to found. */
    public void setFound() {
        this.wasFound = true;
    }

    /** Returns the button that depicts this card. */
    public @Nullable Button getButton() {
        return button;
    }

    /** Sets the given button to depict the card. Button should not be set more than once. */
    public void addButton(@NotNull Button button) {
        checkState(this.button == null); //should not add a button more than once.
        this.button = button;
    }
}
