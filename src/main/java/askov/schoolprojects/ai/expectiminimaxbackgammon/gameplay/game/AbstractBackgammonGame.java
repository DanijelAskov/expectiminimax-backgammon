/*
 * Copyright (C) 2017  Danijel Askov
 *
 * This file is part of Expectiminimax Backgammon.
 *
 * Expectiminimax Backgammon is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Expectiminimax Backgammon is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.gameplay.game;

import askov.schoolprojects.ai.expectiminimaxbackgammon.gameplay.player.Player;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Board;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Checker;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.CheckerStackIndexOutOfBoundsException;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Die;
import askov.schoolprojects.ai.expectiminimaxbackgammon.util.Util;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Danijel Askov
 */
public abstract class AbstractBackgammonGame {

    public static final int NUM_PLAYERS = 2;

    protected final Board board;
    protected final Checker[] checkers;
    private final Player[] players = new Player[NUM_PLAYERS];

    protected static final int[] POINT_INDICES = {6, 8, 13, 24};
    private static final int[] NUM_CHECKERS = {5, 3, 5, 2};
    
    private static final int NUM_CHECKERS_PER_PLAYER = 15;

    protected Player currentPlayer;
    private int currentPlayerIndex = 0;

    private Player winner;

    private Stage stage;

    protected GameState gameState = GameState.WAITING_FOR_GAME_TO_START;

    public AbstractBackgammonGame(double boardWidth, double boardHeight, Player... players) {
        board = new Board(boardWidth, boardHeight);

        for (int i = 0; i < NUM_PLAYERS; i++) {
            this.players[i] = players[i];
            this.players[i].setBoard(board);
        }
        
        int totalNumCheckers = 0;
        for (int numChecker : NUM_CHECKERS) {
            totalNumCheckers += numChecker;
        }
        totalNumCheckers *= NUM_PLAYERS;
        checkers = new Checker[totalNumCheckers];
        
        int currentChecker = 0;
        for (int k = 0; k < NUM_PLAYERS; k++) {
            int j = 0;
            for (int index : POINT_INDICES) {
                for (int i = 0; i < NUM_CHECKERS[j]; i++)
                    try {
                        checkers[currentChecker++] = board.createChecker(players[k].getCheckerColor(), index);
                    } catch (CheckerStackIndexOutOfBoundsException ex) {
                        Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                    }
                j++;
            }
        }

        board.update();

        currentPlayer = players[currentPlayerIndex];
        // System.out.println("Current Game State: " + gameState);
        updateGameState(GameAction.GAME_STARTED);
    }

    public abstract void updateGameState(GameAction gameAction);
    
    public Board getBoard() {
        return board;
    }
    
    public Player getWinner() {
        if (winner != null)
            return winner;
        for (Player player : players) {
            if (board.getHome(player.getCheckerColor()).getNumCheckers() == NUM_CHECKERS_PER_PLAYER) 
                return winner = player;
        }
        return null;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        setTitle();
    }

    protected final void alertEndOfTurn(String content) {
        Alert info = new Alert(Alert.AlertType.WARNING);
        info.initOwner(stage);
        info.setTitle("Expectiminimax Backgammon");
        info.initModality(Modality.APPLICATION_MODAL);
        info.setHeaderText("End of the Turn");
        info.setContentText(content);
        info.showAndWait().ifPresent(response -> updateGameState(GameAction.END_OF_TURN_CONFIRMED));
    }

    protected final void alertGameOverAndExit() {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.initOwner(stage);
        info.setTitle("Expectiminimax Backgammon");
        info.initModality(Modality.APPLICATION_MODAL);
        info.setHeaderText("Game Over");
        info.setContentText(getWinner() + " wins!");
        info.showAndWait();
        Platform.exit();
    }

    protected void switchCurrentPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % NUM_PLAYERS;
        currentPlayer = players[currentPlayerIndex];
        refreshTitle();
    }

    private Player getOtherPlayer() {
        return players[(currentPlayerIndex + 1) % NUM_PLAYERS];
    }

    private void setTitle() {
        stage.setTitle(stage.getTitle() +  " | " + currentPlayer);
    }

    private void refreshTitle() {
        String title = stage.getTitle();
        stage.setTitle(title.substring(0, title.length() - (" | " + getOtherPlayer()).length()) + " | " + currentPlayer);
    }

    protected void addAndShowDice(Die... dice) {
        int i = 0;
        for (Die die : dice) {
            die.setTranslateX((0.20 + i++ * 0.40) * board.getWidth() + Util.generateRandomDouble(0., 0.20 * board.getWidth()));
            die.setTranslateY(0.50 * board.getHeight() - die.getSize() / 2);
            die.setRotate(Util.generateRandomDouble(0., 360));
            if (!board.getChildren().contains(die))
                board.getChildren().add(die);
            die.update();
            die.show();
        }
    }

    protected void removeDice(Die[] dice) {
        for (Die die : dice) {
            board.getChildren().remove(die);
        }
    }
    
}