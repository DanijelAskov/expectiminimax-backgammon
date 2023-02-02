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

package askov.schoolprojects.ai.expectiminimaxbackgammon.sprites;

import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.animation.Animation;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.animation.ChangingColorAnimation;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.elements.IsoscelesTriangle;
import javafx.scene.paint.Color;

/**
 * @author  Danijel Askov
 */
public class Point extends CheckerStack {

    public enum PointColor {

        RED(Color.web("0xDA221D")),
        WHITE(Color.web("0xDEDCAE"));

        private final Color color;

        PointColor(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

    }
    
    private static final int MAX_HEIGHT = 5;

    private final Animation animationPotentialDestination;
    
    public Point(double width, double height, PointColor color) {
        backgroundShape = new IsoscelesTriangle(width, height);
        backgroundShape.setFill(color.getColor());
        backgroundShape.setStrokeWidth(1);
        backgroundShape.setStroke(Color.BLACK);
        super.getChildren().setAll(backgroundShape);

        animationPotentialDestination = new ChangingColorAnimation(backgroundShape);
    }
    
    @Override
    public void animateSelected(boolean selected) {
        if (selected) {
            animationPotentialDestination.start();
        } else {
            animationPotentialDestination.stop();
        }
    }

    @Override
    public void update() {
        super.getChildren().clear();
        super.getChildren().add(backgroundShape);
        int[] height = {MAX_HEIGHT, MAX_HEIGHT - 1};
        int j = 1, k = 0;
        for (Checker checker : checkers) {
            if (j == height[k] + 1) {
                k = (k + 1) % 2;
                j = 1;
            }
            checker.setTranslateX(checker.getSize() / 2);
            checker.setTranslateY((k == 0 ? checker.getSize() / 2 : checker.getSize()) + (j++ - 1) * checker.getSize());
            super.getChildren().add(checker);
        }
    }

}