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

package askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.game;

import askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player.Player;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Board;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Checker;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.CheckerStackIndexOutOfBoundsException;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Die;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Danijel Askov
 */
public abstract class Game {

    private static final int NUM_PLAYERS = 2;

    protected final Board board;
    protected final Checker[] checkers;
    private final Player[] players = new Player[NUM_PLAYERS];

    private static final int[] POINT_INDICES = {6, 8, 13, 24};
    private static final int[] NUM_CHECKERS = {5, 3, 5, 2};
    
    private static final int NUM_CHECKERS_PER_PLAYER = 15;

    protected Player currentPlayer;
    private int currentPlayerIndex = 0;

    private Player winner;

    private Stage stage;

    public Game(double boardWidth, double boardHeight, Player... players) {
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

        currentPlayer = players[currentPlayerIndex];

        try {
            currentPlayer.generatePossibleMoves();
            while (currentPlayer.getPossibleMoves().isEmpty()) {
                currentPlayer.rollDice();
                switchCurrentPlayer();
                currentPlayer.generatePossibleMoves();
            }
        } catch (BoardNotSpecifiedException ex) {
            Logger.getLogger(HumanVsComputerGame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
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

    protected final void alert(Alert.AlertType alertType, String title, String header, String content) {
        Alert info = new Alert(alertType);
        info.initOwner(stage);
        info.setTitle(title);
        info.initModality(Modality.APPLICATION_MODAL);
        info.setHeaderText(header);
        info.setContentText(content);
        info.showAndWait();
    }

    protected final void alertAndExit(Alert.AlertType alertType, String title, String header, String content) {
        alert(alertType, title, header, content);
        Platform.exit();
    }

    protected int numUsedDiceForCurrentPlayer() {
        int numUsedDice = 0;

        Die[] dice = currentPlayer.getDice();
        for (Die die : dice) {
            numUsedDice += die.isUsed() ? 1 : 0;
        }

        return numUsedDice;
    }

    protected void switchCurrentPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % NUM_PLAYERS;
        currentPlayer = players[currentPlayerIndex];
        refreshTitle();
    }

    private Player otherPlayer() {
        return players[(currentPlayerIndex + 1) % NUM_PLAYERS];
    }

    private void setTitle() {
        stage.setTitle(stage.getTitle() +  " | " + currentPlayer);
    }

    private void refreshTitle() {
        String title = stage.getTitle();
        stage.setTitle(title.substring(0, title.length() - (" | " + otherPlayer()).length()) + " | " + currentPlayer);
    }
    
}