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

package askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.animation;

import javafx.animation.Animation;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 * @author  Danijel Askov
 */
public class ChangingColorAnimation extends ShapeAnimation {

    private final FillTransition fillTransition;
    private final Color originalColor;

    public ChangingColorAnimation(Shape shape) {
        super(shape);

        if (shape.getFill() instanceof Color) {
            originalColor = (Color)shape.getFill();

            fillTransition = new FillTransition(Duration.seconds(0.4), shape, Color.YELLOW, Color.DARKORANGE);
            fillTransition.setInterpolator(Interpolator.LINEAR);
            fillTransition.setAutoReverse(true);
            fillTransition.setCycleCount(Animation.INDEFINITE);
        } else {
            fillTransition = null;
            originalColor = null;
        }
    }

    @Override
    public void start() {
        if (fillTransition != null && fillTransition.getStatus() == javafx.animation.Animation.Status.RUNNING) fillTransition.stop();
        fillTransition.play();
    }

    @Override
    public void stop() {
        if (fillTransition != null) fillTransition.stop();
        shape.setFill(originalColor);
    }

}