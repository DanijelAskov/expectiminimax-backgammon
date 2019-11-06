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

package askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.elements;

import javafx.scene.shape.Polygon;

/**
 * @author  Danijel Askov
 */
public class IsoscelesTriangle extends Polygon {

    public IsoscelesTriangle(double base, double height) {
        base = Math.abs(base);
        height = Math.abs(height);
        super.getPoints().setAll(0d, 0d, base / 2, height, base, 0d);
        super.setStrokeMiterLimit(Double.MAX_VALUE);
    }

}