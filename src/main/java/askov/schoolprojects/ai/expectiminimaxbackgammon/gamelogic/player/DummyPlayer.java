/*
 * Copyright (c) 2017, Danijel Askov
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player;

import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Board;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Checker;

public class DummyPlayer extends Player {

    public DummyPlayer(Checker.CheckerColor checkerColor, Board board) {
        super(checkerColor, board);
    }

    public DummyPlayer(Checker.CheckerColor checkerColor) {
        super(checkerColor);
    }

}
