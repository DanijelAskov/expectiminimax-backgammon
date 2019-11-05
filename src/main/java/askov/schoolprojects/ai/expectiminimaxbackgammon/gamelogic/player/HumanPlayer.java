/*
 * Copyright (c) 2017, Danijel Askov
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player;

import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Board;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Checker.CheckerColor;

/**
 * @author  Danijel Askov
 */
public class HumanPlayer extends Player {

    public HumanPlayer(CheckerColor checkerColor, Board board) {
        super(checkerColor, board);
    }
    
    public HumanPlayer(CheckerColor checkerColor) {
        super(checkerColor);
    }

    @Override
    public String toString() {
        return super.toString() + " (Human)";
    }
    
}