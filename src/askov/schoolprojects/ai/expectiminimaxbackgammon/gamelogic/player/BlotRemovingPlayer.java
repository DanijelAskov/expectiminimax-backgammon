/*
 * Copyright (c) 2017, Danijel Askov
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player;

import askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player.move.CheckerRelocation;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player.move.Move;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Board;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Checker;

/**
 * @author  Danijel Askov
 */
public class BlotRemovingPlayer extends ComputerPlayer {

    public BlotRemovingPlayer(Checker.CheckerColor checkerColor, Board board) {
        super(checkerColor, board);
    }

    public BlotRemovingPlayer(Checker.CheckerColor checkerColor) {
        super(checkerColor);
    }

    @Override
    public Move getBestMove() {
        double[] moveQuality = new double[possibleMoves.size()];
        int currentMoveIndex = 0;
        for (Move move : possibleMoves) {
            for (CheckerRelocation checkerRelocation : move.getCheckerRelocations()) {
                if (checkerRelocation.removesBlot()) {
                    moveQuality[currentMoveIndex] += 1.0;
                }
            }
            currentMoveIndex++;
        }
        int maxQualityMoveIndex = -1;
        double maxMoveQuality = 0.0;
        for (int i = 0; i < moveQuality.length; i++) {
            if (moveQuality[i] >= maxMoveQuality) {
                maxMoveQuality = moveQuality[i];
                maxQualityMoveIndex = i;
            }
        }
        return maxQualityMoveIndex != -1 ? possibleMoves.get(maxQualityMoveIndex) : null;
    }

}
