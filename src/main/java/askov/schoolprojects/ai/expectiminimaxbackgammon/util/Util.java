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

package askov.schoolprojects.ai.expectiminimaxbackgammon.util;

import java.util.Random;

/**
 * @author  Danijel Askov
 */
public final class Util {

    private static final Random RANDOM = new Random();

    private Util() { }

    public static double generateRandomDouble(double min, double max) {
        if (Double.valueOf(max - min).isInfinite())
            throw new IllegalArgumentException();
        return (max - min) * RANDOM.nextDouble() + min;
    }

    public static int generateRandomInt(int min, int max) {
        return RANDOM.nextInt((max - min) + 1) + min;
    }

}