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

import askov.schoolprojects.ai.expectiminimaxbackgammon.ExpectiminimaxBackgammon;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gameplay.player.ComputerPlayer;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gameplay.player.ExpectiminimaxPlayer;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gameplay.player.move.CheckerRelocation;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gameplay.player.move.Move;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.*;
import askov.schoolprojects.ai.expectiminimaxbackgammon.util.Util;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Danijel Askov
 */
public class ComputerVsComputerGame extends Game {

    public ComputerVsComputerGame(double boardWidth, double boardHeight) {
        super(boardWidth, boardHeight, new ExpectiminimaxPlayer(Checker.CheckerColor.WHITE), new ExpectiminimaxPlayer(Checker.CheckerColor.BLACK));

        addAndShowDice(currentPlayer.getDice());
        board.update();
    }

    public void play() {
        Move bestMove = ((ComputerPlayer) currentPlayer).getBestMove();
        if (bestMove != null) {
            Task<Void> checkerRelocator = new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    for (CheckerRelocation relocation : bestMove.getCheckerRelocations()) {
                        try {
                            Platform.runLater(() -> {
                                relocation.getChecker().animateSelected(true);
                                relocation.getDestinationCheckerStack().animateSelected(true);
                                if (relocation.removesBlot()) {
                                    relocation.getDestinationCheckerStack().peekChecker().animateBlot(true);
                                }
                                relocation.getDie().hide();

                                board.update();
                            });
                            Thread.sleep(1500);
                            Platform.runLater(() -> {
                                relocation.getChecker().animateSelected(false);
                                relocation.getDestinationCheckerStack().animateSelected(false);

                                Move move = new Move();
                                move.addCheckerRelocation(relocation);
                                currentPlayer.make(move);

                                board.update();
                            });
                        } catch (InterruptedException ex) {
                            Logger.getLogger(HumanVsComputerGame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    Platform.runLater(() -> {
                        try {
                            removeDice(currentPlayer.getDice());
                            if (getWinner() != null) {
                                for (Checker checker : checkers) {
                                    checker.setOnMouseClicked(e -> {
                                    });
                                }
                                for (int i = 1; i <= Board.NUM_POINTS; i++) {
                                    try {
                                        board.getCheckerStack(Checker.CheckerColor.WHITE, i).setOnMouseClicked(e -> {
                                        });
                                    } catch (CheckerStackIndexOutOfBoundsException ex) {
                                        Logger.getLogger(ExpectiminimaxBackgammon.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                alertAndExit(Alert.AlertType.INFORMATION, "Expectiminimax Backgammon", "Game Over", getWinner() + " wins!");
                                return;
                            }
                            for (Die die : currentPlayer.getDice()) {
                                die.setUsed(false);
                            }
                            currentPlayer.rollDice();
                            switchCurrentPlayer();
                            addAndShowDice(currentPlayer.getDice());
                            currentPlayer.generatePossibleMoves();
                            play();
                        } catch (BoardNotSpecifiedException ex) {
                            Logger.getLogger(HumanVsComputerGame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    return null;
                }

            };

            new Thread(checkerRelocator).start();
        } else {
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    Platform.runLater(() -> {
                        try {
                            removeDice(currentPlayer.getDice());
                            for (Die die : currentPlayer.getDice()) {
                                die.setUsed(false);
                            }
                            currentPlayer.rollDice();
                            switchCurrentPlayer();
                            currentPlayer.generatePossibleMoves();
                            addAndShowDice(currentPlayer.getDice());
                            play();
                        } catch (BoardNotSpecifiedException ex) {
                            Logger.getLogger(HumanVsComputerGame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                } catch (InterruptedException ex) {
                    Logger.getLogger(HumanVsComputerGame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }).start();
        }
    }

    private void addAndShowDice(Die... dice) {
        int i = 0;
        for (Die die : dice) {
            die.setTranslateX((0.20 + i++ * 0.40) * board.getWidth() + Util.generateRandomDouble(0., 0.20 * board.getWidth()));
            die.setTranslateY(0.50 * board.getHeight() - die.getSize() / 2);
            die.setRotate(Util.generateRandomDouble(0., 360));
            board.getChildren().add(die);
            die.update();
            die.show();
        }
    }

}