/*
 * Copyright (c) 2017, Danijel Askov
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.game;

import askov.schoolprojects.ai.expectiminimaxbackgammon.ExpectiminimaxBackgammon;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player.move.CheckerRelocation;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player.move.Move;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player.HumanPlayer;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Board;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Checker;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.CheckerStack;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Die;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.CheckerStackIndexOutOfBoundsException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;

/**
 * @author  Danijel Askov
 */
public class HumanVsHumanGame extends Game {

    private Checker pickedUpChecker;
    private Checker prevPickedUpChecker;
    private CheckerStack sourceCheckerStack;
    private CheckerStack prevSourceCheckerStack;
    private List<CheckerStack> destinationCheckerStacks = new ArrayList<>();
    private List<CheckerStack> prevDestinationCheckerStacks = new ArrayList<>();

    public HumanVsHumanGame(double boardWidth, double boardHeight) {
        super(boardWidth, boardHeight, new HumanPlayer(Checker.CheckerColor.WHITE), new HumanPlayer(Checker.CheckerColor.BLACK));

        for (Checker checker : checkers) {
            checker.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    if (pickedUpChecker == checker)
                        return;
                    if (checker.getCheckerColor() != currentPlayer.getCheckerColor())
                        return;
                    boolean foundOnTop = false;
                    for (int j = 0; j <= Board.NUM_POINTS + 1; j++) {
                        try {
                            CheckerStack checkerStack = board.getCheckerStack(currentPlayer.getCheckerColor(), j);
                            if (checkerStack.peekChecker() == checker) {
                                foundOnTop = true;
                                break;
                            }
                        } catch (CheckerStackIndexOutOfBoundsException ex) {
                            Logger.getLogger(HumanVsHumanGame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (!foundOnTop)
                        return;
                    if (pickedUpChecker == null) {
                        List<Move> moves = currentPlayer.getPossibleMoves();
                        for (Move move : moves) {
                            List<CheckerRelocation> relocations = move.getCheckerRelocations();
                            if (relocations.size() > 0) {
                                CheckerRelocation firstRelocation = relocations.get(0);
                                if (firstRelocation.getChecker() == checker) {
                                    if (sourceCheckerStack == null) {
                                        sourceCheckerStack = firstRelocation.getSourceCheckerStack();
                                    }
                                    CheckerStack destinationCheckerStack = firstRelocation.getDestinationCheckerStack();
                                    destinationCheckerStack.animateSelected(true);
                                    destinationCheckerStacks.add(destinationCheckerStack);
                                    Checker topDestinationChecker = firstRelocation.getDestinationCheckerStack().peekChecker();
                                    if (topDestinationChecker != null) {
                                        topDestinationChecker.animateBlot(firstRelocation.removesBlot());
                                    }
                                }
                            }
                        }
                        if (sourceCheckerStack != null) {
                            pickedUpChecker = checker;
                            checker.animateSelected(true);
                            if (prevPickedUpChecker != null) {
                                prevPickedUpChecker.animateSelected(false);
                            }
                            prevPickedUpChecker = null;
                            for (CheckerStack checkerStack : prevDestinationCheckerStacks) {
                                if (!destinationCheckerStacks.contains(checkerStack)) {
                                    checkerStack.animateSelected(false);
                                    Checker topChecker = checkerStack.peekChecker();
                                    if (topChecker != null)
                                        topChecker.animateBlot(false);
                                }
                            }
                            prevDestinationCheckerStacks.clear();
                            prevSourceCheckerStack = null;
                        } else {
                            pickedUpChecker = prevPickedUpChecker;
                            prevPickedUpChecker = null;
                            destinationCheckerStacks = prevDestinationCheckerStacks;
                            prevDestinationCheckerStacks = new ArrayList<>();
                            sourceCheckerStack = prevSourceCheckerStack;
                            prevSourceCheckerStack = null;
                        }
                    } else {
                        prevPickedUpChecker = pickedUpChecker;
                        prevDestinationCheckerStacks = destinationCheckerStacks;
                        prevSourceCheckerStack = sourceCheckerStack;
                        pickedUpChecker = null;
                        destinationCheckerStacks = new ArrayList<>();
                        sourceCheckerStack = null;
                        handle(event);
                    }
                }
            });

        }

        for (int i = 1; i <= Board.NUM_POINTS; i++) {
            try {
                CheckerStack checkerStack = board.getCheckerStack(Checker.CheckerColor.WHITE, i);
                checkerStack.setOnMouseClicked(new CheckerStackClickedHandler(checkerStack));
            } catch (CheckerStackIndexOutOfBoundsException ex) {
                Logger.getLogger(ExpectiminimaxBackgammon.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        CheckerStack checkerStack;
        checkerStack = board.getHome(Checker.CheckerColor.WHITE);
        checkerStack.setOnMouseClicked(new CheckerStackClickedHandler(checkerStack));
        checkerStack = board.getHome(Checker.CheckerColor.BLACK);
        checkerStack.setOnMouseClicked(new CheckerStackClickedHandler(checkerStack));

        addAndShowDice(currentPlayer.getDice());
        board.update();
    }

    private void addAndShowDice(Die... dice) {
        int i = 0;
        for (Die die : dice) {
            die.setTranslateX((0.20 + i++ * 0.40) * board.getWidth() + Math.random() * (0.20 * board.getWidth()));
            die.setTranslateY(0.50 * board.getHeight() - die.getSize() / 2);
            die.setRotate(Math.random() * 360);
            board.getChildren().add(die);
            die.update();
            die.show();
        }
    }
    
    private void removeDice(Die[] dice) {
        for (Die die : dice) {
            board.getChildren().remove(die);
        }
    }

    private class CheckerStackClickedHandler implements EventHandler<MouseEvent> {

        private final CheckerStack checkerStack;

        public CheckerStackClickedHandler(CheckerStack checkerStack) {
            this.checkerStack = checkerStack;
        }

        @Override
        public void handle(MouseEvent event) {
            if (pickedUpChecker != null && destinationCheckerStacks.contains(checkerStack)) {
                CheckerRelocation checkerRelocation = new CheckerRelocation(sourceCheckerStack, checkerStack, pickedUpChecker);
                boolean relocationFound = false;
                for (Move possibleMove : currentPlayer.getPossibleMoves()) {
                    for (CheckerRelocation cR : possibleMove.getCheckerRelocations()) {
                        if (cR.equals(checkerRelocation)) {
                            if (!relocationFound) {
                                Move move = new Move();
                                move.addCheckerRelocation(cR);
                                currentPlayer.make(move);
                                cR.getDie().hide();

                                pickedUpChecker.animateSelected(false);
                                for (CheckerStack cS : destinationCheckerStacks) {
                                    cS.animateSelected(false);
                                    Checker topChecker = cS.peekChecker();
                                    if (topChecker != null)
                                        topChecker.animateBlot(false);
                                }
                                board.update();
                                destinationCheckerStacks.clear();
                                pickedUpChecker = null;
                                sourceCheckerStack = null;
                                relocationFound = true;
                                break;
                            }
                        }
                        if (relocationFound) {
                            break;
                        }
                    }
                }
                if (relocationFound) {
                    try {
                        currentPlayer.generatePossibleMoves();
                    } catch (BoardNotSpecifiedException ex) {
                        Logger.getLogger(HumanVsHumanGame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            if (currentPlayer.getPossibleMoves().isEmpty()) {
                if (getWinner() != null) {
                    for (Checker checker : checkers)
                        checker.setOnMouseClicked(e -> {});
                    for (int i = 1; i <= Board.NUM_POINTS; i++) {
                        try {
                            board.getCheckerStack(Checker.CheckerColor.WHITE, i).setOnMouseClicked(e -> {});
                        } catch (CheckerStackIndexOutOfBoundsException ex) {
                            Logger.getLogger(ExpectiminimaxBackgammon.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    alertAndExit(Alert.AlertType.INFORMATION, "Expectiminimax Backgammon", "Game Over", getWinner() + " wins!");
                } else {
                    int numUsedDice = numUsedDiceForCurrentPlayer();
                    if (numUsedDice == 0) {
                        alert(Alert.AlertType.WARNING, "Expectiminimax Backgammon", "End of the Turn", "No possible moves for the given dice combination for the " + currentPlayer);
                    } else if (numUsedDice == 1) {
                        alert(Alert.AlertType.WARNING, "Expectiminimax Backgammon", "End of the Turn", "No more possible moves for the " + currentPlayer);
                    }

                    removeDice(currentPlayer.getDice());
                    for (Die die : currentPlayer.getDice()) {
                        die.setUsed(false);
                    }
                    currentPlayer.rollDice();
                    switchCurrentPlayer();
                    try {
                        currentPlayer.generatePossibleMoves();
                    } catch (BoardNotSpecifiedException ex) {
                        Logger.getLogger(HumanVsHumanGame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    addAndShowDice(currentPlayer.getDice());
                    if (currentPlayer.getPossibleMoves().isEmpty()) {
                        handle(event);
                    }
                }
            }
        }
    }
    
}