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

import javafx.animation.ScaleTransition;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 * @author  Danijel Askov
 */
public class ScalingAnimation extends ShapeAnimation {

    private final ScaleTransition scaleTransition;

    public ScalingAnimation(Shape shape) {
        super(shape);

        scaleTransition = new ScaleTransition(Duration.seconds(0.40), shape);
        scaleTransition.setFromX(1);
        scaleTransition.setToX(1.5);
        scaleTransition.setFromY(1);
        scaleTransition.setToY(1.5);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setCycleCount(javafx.animation.Animation.INDEFINITE);
    }

    @Override
    public void start() {
        if (scaleTransition.getStatus() == javafx.animation.Animation.Status.RUNNING) scaleTransition.stop();
        scaleTransition.play();
    }

    @Override
    public void stop() {
        scaleTransition.stop();
    }
}