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

public enum GameState {

    WAITING_FOR_GAME_TO_START("WAITING_FOR_GAME_TO_START"),

    WAITING_FOR_DICE_ROLL("WAITING_FOR_DICE_ROLL"),
    WAITING_FOR_FIRST_RELOCATION("WAITING_FOR_FIRST_RELOCATION"),
    WAITING_FOR_SECOND_RELOCATION("WAITING_FOR_SECOND_RELOCATION"),
    WAITING_END_OF_TURN_CONFIRMATION("WAITING_FOR_TURN_END_CONFIRMATION");

    final String description;

    GameState(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.description;
    }

}
