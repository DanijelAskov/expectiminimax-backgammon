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
