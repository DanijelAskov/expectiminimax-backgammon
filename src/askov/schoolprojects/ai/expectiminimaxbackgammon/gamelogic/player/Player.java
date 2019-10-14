/*
 * Copyright (c) 2017, Danijel Askov
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player;

import askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.game.BoardNotSpecifiedException;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player.move.CheckerRelocation;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player.move.Move;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Board;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Checker;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Checker.CheckerColor;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.CheckerStack;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Die;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.CheckerStackIndexOutOfBoundsException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Danijel Askov
 */
public abstract class Player {

    private static final int NUM_DICE = 2;

    private final CheckerColor checkerColor;
    protected Board board;
    private Die[] dice = new Die[NUM_DICE];

    protected List<Move> possibleMoves = new ArrayList<>();

    public Player(CheckerColor checkerColor, Board board) {
        this.checkerColor = checkerColor;
        this.board = board;
        if (board != null) {
            for (int i = 0; i < NUM_DICE; i++) {
                dice[i] = new Die(0.05 * board.getWidth());
                dice[i].roll();
            }
        }
    }

    public Player(CheckerColor checkerColor) {
        this(checkerColor, null);
    }

    public void setBoard(Board board) {
        if (this.board == null) {
            for (int i = 0; i < NUM_DICE; i++) {
                dice[i] = new Die(0.05 * board.getWidth());
                dice[i].roll();
            }
        }
        this.board = board;
    }

    public CheckerColor getCheckerColor() {
        return checkerColor;
    }

    public Die[] getDice() {
        return dice;
    }

    public void rollDice() {
        for (Die die : dice) {
            die.roll();
        }
    }

    public void setDice(Die ...dice) {
        this.dice = dice;
    }

    public void generatePossibleMoves() throws BoardNotSpecifiedException {
        possibleMoves = new ArrayList<>();
        generateMoves();
    }

    private CheckerRelocation checkerRelocation1;
    private CheckerRelocation checkerRelocation2;

    private void generateMoves() throws BoardNotSpecifiedException {
        if (board == null) {
            throw new BoardNotSpecifiedException();
        }
        boolean barContainsCheckers = !board.getBar(checkerColor).isEmpty();
        for (Die die : dice) {
            if (!die.isUsed()) {
                CheckerStack sourceCheckerStack;
                Checker sourceCheckerStackTopChecker;
                CheckerStack destCheckerStack;
                Checker destCheckerStackTopChecker;
                if (barContainsCheckers) {

                    try {
                        sourceCheckerStack = board.getBar(checkerColor);
                        sourceCheckerStackTopChecker = sourceCheckerStack.peekChecker();
                        destCheckerStack = board.getCheckerStack(checkerColor, (Board.NUM_POINTS + 1) - die.getValue());
                        destCheckerStackTopChecker = destCheckerStack.peekChecker();
                        if ((destCheckerStack.getNumCheckers() < 2 || destCheckerStackTopChecker.getCheckerColor() == checkerColor) && (destCheckerStack != board.getHome(checkerColor) || allCheckersOnHomeBoard())) {
                            if (checkerRelocation1 == null) {
                                checkerRelocation1 = new CheckerRelocation(sourceCheckerStack, destCheckerStack, sourceCheckerStackTopChecker, die);
                                checkerRelocation1.setSourcePointIndex(Board.NUM_POINTS + 1);
                                checkerRelocation1.setDestinationPointIndex((Board.NUM_POINTS + 1) - die.getValue());
                                checkerRelocation1.relocateForward();
                                int numMoves = possibleMoves.size();
                                generateMoves();
                                checkerRelocation1.relocateBackward();
                                if (possibleMoves.size() - numMoves == 0) {
                                    Move move = new Move();
                                    move.addCheckerRelocation(checkerRelocation1);
                                    possibleMoves.add(move);
                                }
                                checkerRelocation1 = null;
                            } else {
                                checkerRelocation2 = new CheckerRelocation(sourceCheckerStack, destCheckerStack, sourceCheckerStackTopChecker, die);
                                checkerRelocation2.setSourcePointIndex(Board.NUM_POINTS + 1);
                                checkerRelocation2.setDestinationPointIndex((Board.NUM_POINTS + 1) - die.getValue());
                                Move move = new Move();
                                move.addCheckerRelocations(checkerRelocation1, checkerRelocation2);
                                possibleMoves.add(move);
                                checkerRelocation2 = null;
                            }
                        }
                    } catch (CheckerStackIndexOutOfBoundsException ex) {
                        Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    for (int i = Board.NUM_POINTS; i >= 0; i--) {

                        try {
                            sourceCheckerStack = board.getCheckerStack(checkerColor, i);
                            sourceCheckerStackTopChecker = sourceCheckerStack.peekChecker();
                            if (sourceCheckerStackTopChecker == null || sourceCheckerStackTopChecker.getCheckerColor() != checkerColor || i - die.getValue() < 0)
                                continue;
                            destCheckerStack = board.getCheckerStack(checkerColor, i - die.getValue());
                            destCheckerStackTopChecker = destCheckerStack.peekChecker();
                            if ((destCheckerStack.getNumCheckers() < 2 || destCheckerStackTopChecker.getCheckerColor() == checkerColor) && (destCheckerStack != board.getHome(checkerColor) || allCheckersOnHomeBoard())) {
                                if (checkerRelocation1 == null) {
                                    checkerRelocation1 = new CheckerRelocation(sourceCheckerStack, destCheckerStack, sourceCheckerStackTopChecker, die);
                                    checkerRelocation1.setSourcePointIndex(i);
                                    checkerRelocation1.setDestinationPointIndex(i - die.getValue());
                                    int numMoves = possibleMoves.size();
                                    checkerRelocation1.relocateForward();
                                    generateMoves();
                                    if (possibleMoves.size() - numMoves == 0) {
                                        Move move = new Move();
                                        move.addCheckerRelocation(checkerRelocation1);
                                        possibleMoves.add(move);
                                    }
                                    checkerRelocation1.relocateBackward();
                                    checkerRelocation1 = null;
                                } else {
                                    checkerRelocation2 = new CheckerRelocation(sourceCheckerStack, destCheckerStack, sourceCheckerStackTopChecker, die);
                                    checkerRelocation2.setSourcePointIndex(i);
                                    checkerRelocation2.setDestinationPointIndex(i - die.getValue());
                                    Move move = new Move();
                                    move.addCheckerRelocations(checkerRelocation1, checkerRelocation2);
                                    possibleMoves.add(move);
                                    checkerRelocation2 = null;
                                }
                            }
                        } catch (CheckerStackIndexOutOfBoundsException ex) {
                            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            }
        }
    }

    private boolean allCheckersOnHomeBoard() {
        for (int i = Board.NUM_POINTS / 4 + 1; i <= Board.NUM_POINTS; i++) {
            CheckerStack checkerStack;
            try {
                checkerStack = board.getCheckerStack(checkerColor, i);
                Checker checker = checkerStack.peekChecker();
                if (checker != null && checker.getCheckerColor() == checkerColor) {
                    return false;
                }
            } catch (CheckerStackIndexOutOfBoundsException ex) {
                Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    public List<Move> getPossibleMoves() {
        return possibleMoves;
    }

    public void make(Move move) {
        List<CheckerRelocation> relocations = move.getCheckerRelocations();
        for (CheckerRelocation checkerRelocation : relocations) {
            if (checkerRelocation.removesBlot()) {
                Checker blot = checkerRelocation.getDestinationCheckerStack().popChecker();
                board.getBar(Checker.CheckerColor.getOppositeCheckerColor(getCheckerColor())).pushChecker(blot);
                blot.animateBlot(false);
            }
            checkerRelocation.relocateForward();
        }
    }

    public void unmake(Move move) {
        List<CheckerRelocation> relocations = move.getCheckerRelocations();
        for (int i = relocations.size() - 1; i > -1; i--) {
            CheckerRelocation relocation = relocations.get(i);
            relocation.relocateBackward();
            if (relocation.removesBlot()) {
                Checker blot = board.getBar(Checker.CheckerColor.getOppositeCheckerColor(getCheckerColor())).popChecker();
                relocation.getDestinationCheckerStack().pushChecker(blot);
            }
        }
    }

    @Override
    public String toString() {
        return "Player with the " + checkerColor + " checkers";
    }

}