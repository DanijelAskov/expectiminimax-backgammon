/*
 * Copyright (c) 2017, Danijel Askov
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player;

import askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player.move.Move;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Board;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Checker;

/**
 * @author Danijel Askov
 */
public abstract class ComputerPlayer extends Player {

    public ComputerPlayer(Checker.CheckerColor checkerColor, Board board) {
        super(checkerColor, board);
    }

    public ComputerPlayer(Checker.CheckerColor checkerColor) {
        super(checkerColor);
    }

    public abstract Move getBestMove();

    @Override
    public String toString() {
        return super.toString() + " (Computer)";
    }

}