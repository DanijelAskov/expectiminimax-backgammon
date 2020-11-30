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
import askov.schoolprojects.ai.expectiminimaxbackgammon.gameplay.player.HumanPlayer;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gameplay.player.Player;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gameplay.player.move.CheckerRelocation;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gameplay.player.move.Move;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Danijel Askov
 */
public class BackgammonGame extends AbstractBackgammonGame {

    private Checker pickedUpChecker;
    private Checker prevPickedUpChecker;
    private CheckerStack sourceCheckerStack;
    private CheckerStack prevSourceCheckerStack;
    private List<CheckerStack> destinationCheckerStacks = new ArrayList<>();
    private List<CheckerStack> prevDestinationCheckerStacks = new ArrayList<>();

    public BackgammonGame(double boardWidth, double boardHeight, Player... players) {
        super(boardWidth, boardHeight, players);
    }

    @Override
    public void updateGameState(GameAction gameAction) {
        switch (gameState) {
            case WAITING_FOR_GAME_TO_START:
                if (gameAction == GameAction.GAME_STARTED) {
                    gameState = GameState.WAITING_FOR_DICE_ROLL;
                    // System.out.println(currentPlayer.toString() + ": " + gameState);
                    updateGameState(GameAction.NULL);
                }
                break;
            case WAITING_FOR_DICE_ROLL:
                try {
                    currentPlayer.rollDice();
                    addAndShowDice(currentPlayer.getDice());
                    board.update();
                    currentPlayer.generatePossibleMoves();
                    if (currentPlayer.getPossibleMoves().isEmpty()) {
                        gameState = GameState.WAITING_END_OF_TURN_CONFIRMATION;
                        alertEndOfTurn("No possible moves for the given dice combination for the " + currentPlayer);
                        // System.out.println(currentPlayer.toString() + ": " + gameState);
                        break;
                    }
                } catch (BoardNotSpecifiedException ex) {
                    Logger.getLogger(BackgammonGame.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (currentPlayer instanceof HumanPlayer) {
                    for (Checker checker : checkers)
                    {
                        checker.setOnMouseEntered(new ChangeCursorIconIfCheckerIsMovable(checker));
                        checker.setOnMouseClicked(new AnimateCheckerIfOnTopAndSelectable(checker));
                    }
                    for (int i = 0; i < Board.NUM_POINTS + 1; i++) {
                        CheckerStack checkerStack = null;
                        try {
                            checkerStack = board.getCheckerStack(currentPlayer.getCheckerColor(), i);
                        } catch (CheckerStackIndexOutOfBoundsException ex) {
                            Logger.getLogger(BackgammonGame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        checkerStack.setOnMouseEntered(new ChangeCursorIconIfPointIsPossibleDestination(checkerStack));
                        checkerStack.setOnMouseClicked(new RelocateCheckerWhenCheckerStackClicked(checkerStack, GameAction.FIRST_RELOCATION_DEFINED));
                    }
                    CheckerStack checkerStack;
                    checkerStack = board.getHome(currentPlayer.getCheckerColor());
                    checkerStack.setOnMouseClicked(new RelocateCheckerWhenCheckerStackClicked(checkerStack, GameAction.FIRST_RELOCATION_DEFINED));
                } else {
                    Move bestMove = ((ComputerPlayer) currentPlayer).getBestMove();
                    if (bestMove != null) {
                        int numRelocations = bestMove.getCheckerRelocations().size();
                        Task<Void> checkerRelocator = new Task<>() {

                            @Override
                            protected Void call() throws Exception {
                                int currentRelocation = 1;
                                for (CheckerRelocation relocation : bestMove.getCheckerRelocations()) {
                                    Platform.runLater(() -> {
                                        relocation.getChecker().animateSelected(true);
                                        relocation.getDestinationCheckerStack().animateSelected(true);
                                        if (relocation.removesBlot()) {
                                            relocation.getDestinationCheckerStack().peekChecker().animateBlot(true);
                                        }
                                        relocation.getDie().hide();

                                        board.update();
                                    });
                                    Thread.sleep((long)(Die.TIME_TO_HIDE * 1000));
                                    int finalCurrentRelocation = currentRelocation;
                                    Platform.runLater(() -> {
                                        relocation.getChecker().animateSelected(false);
                                        relocation.getDestinationCheckerStack().animateSelected(false);

                                        Move move = new Move();
                                        move.addCheckerRelocation(relocation);
                                        currentPlayer.make(move);

                                        board.update();

                                        if (numRelocations == 1) {
                                            gameState = GameState.WAITING_END_OF_TURN_CONFIRMATION;
                                            alertEndOfTurn("No more possible moves for the " + currentPlayer);
                                            // System.out.println(currentPlayer.toString() + ": " + gameState);
                                        } else {
                                            updateGameState(finalCurrentRelocation == 1 ? GameAction.FIRST_RELOCATION_DEFINED : GameAction.SECOND_RELOCATION_DEFINED);
                                        }
                                    });
                                    currentRelocation++;
                                }
                                return null;
                            }

                        };

                        new Thread(checkerRelocator).start();
                    }
                }
                gameState = GameState.WAITING_FOR_FIRST_RELOCATION;
                // System.out.println(currentPlayer.toString() + ": " + gameState);
                break;
            case WAITING_FOR_FIRST_RELOCATION:
                if (gameAction == GameAction.FIRST_RELOCATION_DEFINED) {
                    try {
                        currentPlayer.generatePossibleMoves();
                    } catch (BoardNotSpecifiedException ex) {
                        Logger.getLogger(BackgammonGame.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (currentPlayer.getPossibleMoves().isEmpty()) {
                        gameState = GameState.WAITING_END_OF_TURN_CONFIRMATION;
                        alertEndOfTurn("No more possible moves for the " + currentPlayer);
                    } else {
                        for (int i = 1; i <= Board.NUM_POINTS; i++) {
                            CheckerStack checkerStack = null;
                            try {
                                checkerStack = board.getCheckerStack(currentPlayer.getCheckerColor(), i);
                            } catch (CheckerStackIndexOutOfBoundsException ex) {
                                Logger.getLogger(BackgammonGame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            checkerStack.setOnMouseClicked(new RelocateCheckerWhenCheckerStackClicked(checkerStack, GameAction.SECOND_RELOCATION_DEFINED));
                        }
                        CheckerStack checkerStack;
                        checkerStack = board.getHome(currentPlayer.getCheckerColor());
                        checkerStack.setOnMouseClicked(new RelocateCheckerWhenCheckerStackClicked(checkerStack, GameAction.SECOND_RELOCATION_DEFINED));
                        gameState = GameState.WAITING_FOR_SECOND_RELOCATION;
                    }
                    // System.out.println(currentPlayer.toString() + ": " + gameState);
                }
                break;
            case WAITING_FOR_SECOND_RELOCATION:
                if (gameAction == GameAction.SECOND_RELOCATION_DEFINED) {
                    removeDice(currentPlayer.getDice());
                    for (Die die : currentPlayer.getDice()) {
                        die.setUsed(false);
                    }
                    if (getWinner() != null) {
                        for (Checker checker : checkers) {
                            checker.setOnMouseEntered(e -> {});
                            checker.setOnMouseClicked(e -> {});
                        }
                        for (int i = 1; i <= Board.NUM_POINTS; i++) {
                            try {
                                board.getCheckerStack(Checker.CheckerColor.WHITE, i).setOnMouseClicked(e -> {});
                            } catch (CheckerStackIndexOutOfBoundsException ex) {
                                Logger.getLogger(ExpectiminimaxBackgammon.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        alertGameOverAndExit();
                        return;
                    } else {
                        switchCurrentPlayer();
                        gameState = GameState.WAITING_FOR_DICE_ROLL;
                        // System.out.println(currentPlayer.toString() + ": " + gameState);
                        updateGameState(GameAction.NULL);
                    }
                }
                break;
            case WAITING_END_OF_TURN_CONFIRMATION:
                if (gameAction == GameAction.END_OF_TURN_CONFIRMED) {
                    removeDice(currentPlayer.getDice());
                    for (Die die : currentPlayer.getDice()) {
                        die.setUsed(false);
                    }
                    if (getWinner() != null) {
                        for (Checker checker : checkers) {
                            checker.setOnMouseEntered(e -> {});
                            checker.setOnMouseClicked(e -> {});
                        }
                        for (int i = 1; i <= Board.NUM_POINTS; i++) {
                            try {
                                board.getCheckerStack(Checker.CheckerColor.WHITE, i).setOnMouseClicked(e -> {});
                            } catch (CheckerStackIndexOutOfBoundsException ex) {
                                Logger.getLogger(ExpectiminimaxBackgammon.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        alertGameOverAndExit();
                        return;
                    } else {
                        switchCurrentPlayer();
                        gameState = GameState.WAITING_FOR_DICE_ROLL;
                        // System.out.println(currentPlayer.toString() + ": " + gameState);
                        updateGameState(GameAction.NULL);
                    }
                }
                break;
        }
    }

    private class ChangeCursorIconIfCheckerIsMovable implements EventHandler<MouseEvent> {

        private Checker checker;

        public ChangeCursorIconIfCheckerIsMovable(Checker checker) {
            this.checker = checker;
        }

        @Override
        public void handle(MouseEvent mouseEvent) {
            boolean checkerIsMovable = false;
            if (currentPlayer instanceof HumanPlayer && checker.getCheckerColor() == currentPlayer.getCheckerColor()) {
                if (getBoard().getBar(checker.getCheckerColor()).isEmpty()) {
                    List<Move> possibleMovesForCurrentPlayer = currentPlayer.getPossibleMoves();
                    for (Move move : possibleMovesForCurrentPlayer) {
                        for (CheckerRelocation checkerRelocation : move.getCheckerRelocations()) {
                            if (checkerRelocation.getSourceCheckerStack().peekChecker() == checker) {
                                checkerIsMovable = true;
                                break;
                            }
                        }
                        if (checkerIsMovable) break;
                    }
                } else checkerIsMovable = getBoard().getBar(checker.getCheckerColor()).peekChecker() == checker;
            }
            checker.setCursor(checkerIsMovable ? Cursor.HAND : Cursor.DEFAULT);
        }

    }

    private class AnimateCheckerIfOnTopAndSelectable implements EventHandler<MouseEvent> {

        private Checker checker;

        public AnimateCheckerIfOnTopAndSelectable(Checker checker) {
            this.checker = checker;
        }

        @Override
        public void handle(MouseEvent event) {
            if (pickedUpChecker == checker) {
                return;
            }
            if (!(currentPlayer instanceof HumanPlayer)) {
                return;
            }
            if (checker.getCheckerColor() != currentPlayer.getCheckerColor()) {
                return;
            }
            boolean foundOnTop = false;
            for (int j = 0; j <= Board.NUM_POINTS + 1; j++) {
                try {
                    CheckerStack checkerStack = board.getCheckerStack(currentPlayer.getCheckerColor(), j);
                    if (checkerStack.peekChecker() == checker) {
                        foundOnTop = true;
                        break;
                    }
                } catch (CheckerStackIndexOutOfBoundsException ex) {
                    Logger.getLogger(BackgammonGame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (!foundOnTop) {
                return;
            }
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
                            if (topChecker != null) {
                                topChecker.animateBlot(false);
                            }
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

    }

    private class ChangeCursorIconIfPointIsPossibleDestination implements EventHandler<MouseEvent> {

        private final CheckerStack checkerStack;

        public ChangeCursorIconIfPointIsPossibleDestination(CheckerStack checkerStack) {
            this.checkerStack = checkerStack;
        }

        @Override
        public void handle(MouseEvent event) {
            boolean checkerStackIsPossibleDestination = false;
            if (currentPlayer instanceof HumanPlayer && pickedUpChecker != null && destinationCheckerStacks.contains(checkerStack)) {
                List<Move> possibleMovesForCurrentPlayer = currentPlayer.getPossibleMoves();
                for (Move move : possibleMovesForCurrentPlayer) {
                    for (CheckerRelocation checkerRelocation : move.getCheckerRelocations()) {
                        if (checkerRelocation.getSourceCheckerStack().peekChecker() == pickedUpChecker && checkerRelocation.getDestinationCheckerStack() == checkerStack) {
                            checkerStackIsPossibleDestination = true;
                            break;
                        }
                    }
                    if (checkerStackIsPossibleDestination) break;
                }
            }
            checkerStack.setCursor(checkerStackIsPossibleDestination ? Cursor.HAND : Cursor.DEFAULT);
        }

    }

    private class RelocateCheckerWhenCheckerStackClicked implements EventHandler<MouseEvent> {

        private final CheckerStack checkerStack;
        private final GameAction gameAction;

        public RelocateCheckerWhenCheckerStackClicked(CheckerStack checkerStack, GameAction gameActionOnClick) {
            this.checkerStack = checkerStack;
            this.gameAction = gameActionOnClick;
        }

        @Override
        public void handle(MouseEvent event) {
            if (!(currentPlayer instanceof HumanPlayer)) {
                return;
            }
            if (pickedUpChecker != null && destinationCheckerStacks.contains(checkerStack)) {
                CheckerRelocation checkerRelocation = new CheckerRelocation(sourceCheckerStack, checkerStack, pickedUpChecker);
                boolean relocationFound = false;
                for (Move possibleMove : currentPlayer.getPossibleMoves()) {
                    for (CheckerRelocation currentCheckerRelocation : possibleMove.getCheckerRelocations()) {
                        if (currentCheckerRelocation.equals(checkerRelocation)) {
                            if (!relocationFound) {
                                Task<Void> checkerRelocator = new Task<>() {

                                    @Override
                                    protected Void call() throws Exception {
                                        Platform.runLater(() -> {
                                            Move move = new Move();
                                            move.addCheckerRelocation(currentCheckerRelocation);
                                            currentPlayer.make(move);
                                            currentCheckerRelocation.getDie().hide();

                                            pickedUpChecker.animateSelected(false);
                                            for (CheckerStack checkerStack : destinationCheckerStacks) {
                                                checkerStack.animateSelected(false);
                                                Checker topChecker = checkerStack.peekChecker();
                                                if (topChecker != null) {
                                                    topChecker.animateBlot(false);
                                                }
                                            }
                                            for (Checker checker : checkers) {
                                                checker.setOnMouseEntered(e -> {});
                                                checker.setOnMouseClicked(e -> {});
                                            }

                                            board.update();
                                            destinationCheckerStacks.clear();
                                            pickedUpChecker = null;
                                            sourceCheckerStack = null;
                                        });
                                        Thread.sleep((long)(Die.TIME_TO_HIDE * 1000));
                                        Platform.runLater(() -> {
                                            for (Checker checker : checkers) {
                                                checker.setOnMouseEntered(new ChangeCursorIconIfCheckerIsMovable(checker));
                                                checker.setOnMouseClicked(new AnimateCheckerIfOnTopAndSelectable(checker));
                                            }
                                            updateGameState(gameAction);
                                        });

                                        return null;
                                    }

                                };
                                new Thread(checkerRelocator).start();
                                relocationFound = true;
                                break;
                            }
                        }
                        if (relocationFound) {
                            break;
                        }
                    }
                }
            }
        }
    }

}