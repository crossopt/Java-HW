package ru.hse.crossopt.findpairs;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkState;

/** Implementation of a game where the player should find pairs on a board. */
public class FindPairs extends Application {
    private final static int WINDOW_HEIGHT = 300;
    private final static int WINDOW_WIDTH = 300;
    private final static int DEFAULT_BOARD_SIZE = 4;

    private static int boardSize = 1;
    private boolean refuseClicks = false;
    private GameBoard board;
    private @Nullable Card openCard;

    /** Starts the application, shows a message with the rules and starts the game. */
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Pairs");
        GridPane boardGrid = new GridPane();

        for (int row = 0; row < boardSize; row++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setFillHeight(true);
            rowConstraints.setVgrow(Priority.ALWAYS);
            rowConstraints.setPercentHeight(100.0 / boardSize);
            boardGrid.getRowConstraints().add(rowConstraints);
        }
        for (int col = 0; col < boardSize; col++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setFillWidth(true);
            columnConstraints.setHgrow(Priority.ALWAYS);
            columnConstraints.setPercentWidth(100.0 / boardSize);
            boardGrid.getColumnConstraints().add(columnConstraints);
        }

        board = new GameBoard(boardSize);
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                boardGrid.add(createButton(board.get(x, y)), x, y);
            }
        }

        stage.setScene(new Scene(boardGrid));
        stage.setHeight(WINDOW_HEIGHT);
        stage.setWidth(WINDOW_WIDTH);
        stage.show();
        alertAboutRules();
    }

    /** Creates a button for the given card. */
    private Button createButton(Card card) {
        var button = new Button();
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setOnAction(event -> handleCardChosen(card, event));
        card.addButton(button);
        return button;
    }

    /** Implements the changing of the game's state upon a card being chosen. */
    private void handleCardChosen(@NotNull Card card, ActionEvent event) {
        Button source = (Button)event.getSource();
        if (refuseClicks || card.wasFound()) {
            return; //no action required for found number, or if last delay is not finished.
        } else if (card.equals(openCard)) { //double-click closes card.
            matchOpenCard(false);
            return;
        }
        source.setText(Integer.valueOf(card.getNumber()).toString()); //open the clicked card.
        if (openCard == null) {
            openCard = card; //no further action required.
        } else {
            if (openCard.getNumber() == card.getNumber()) { //pair was found.
                card.setFound();
                matchOpenCard(true);

                if (board.isWin()) {
                    alertAboutWin();
                }
            } else { //not a match, wait and close both cards.
                refuseClicks = true; //refuse button presses until delay finishes.
                var delay = new PauseTransition(Duration.seconds(0.5));
                delay.setOnFinished(delayEvent -> {
                    source.setText("");
                    matchOpenCard(false);
                    refuseClicks = false;
                });
                delay.play();
            }
        }
    }

    /** Executes the necessary actions for the button that was already open when the current one was pressed. */
    private void matchOpenCard(boolean wasFound) {
        checkState(openCard != null);
        Button openButton = openCard.getButton();
        if (!wasFound) {
            assert openButton != null; //buttons are initialized in-game, 100%.
            openButton.setText("");
        } else {
            openCard.setFound();
        }
        openCard = null;
    }

    /** Shows a message that the player won the game. */
    private static void alertAboutWin() {
        var alert = new Alert(Alert.AlertType.INFORMATION, "Congratulations! You won!", ButtonType.OK);
        alert.showAndWait();
        Platform.exit();
    }

    /** Shows a message explaining the rules of the game. */
    private static void alertAboutRules() {
        var alert = new Alert(Alert.AlertType.INFORMATION, "This is a Pairs game.\n" +
                "You should find all pairs of numbers.\n" +
                "The numbers are from 0 to " + (boardSize * boardSize - 1) / 2 + ".", ButtonType.OK);
        alert.showAndWait();
    }

    /**
     * Starts a game with the given board size, or with a default board size if the given size is incorrect.
     * @param args the size of the game board.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Require 1 argument: the size of the board.");
            boardSize = DEFAULT_BOARD_SIZE;
        } else {
            try {
                boardSize = Integer.parseInt(args[0]);
            } catch (NumberFormatException exception) {
                System.out.println("Size of the board should be an integer.");
                boardSize = DEFAULT_BOARD_SIZE;
            }
        }
        if (boardSize % 2 != 0 || boardSize <= 0 || boardSize > GameBoard.LARGEST_BOARD_SIZE) {
            System.out.println("Size of the board should be an even integer.");
            System.out.println("It should be greater than 0 and no greater than " + GameBoard.LARGEST_BOARD_SIZE + ".");
            boardSize = DEFAULT_BOARD_SIZE;
        }
        launch();
    }
}
