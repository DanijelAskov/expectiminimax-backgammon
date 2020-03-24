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

import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Sprite;

import javafx.animation.*;
import javafx.animation.Animation;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

/**
 * @author  Danijel Askov
 */
public class DashedCircleAnimation extends SpriteAnimation {

    public enum RotateDirection {
        CLOCKWISE(1.), ANTICLOCKWISE(-1.);

        private double multiplier;

        RotateDirection(double multiplier) {
            this.multiplier = multiplier;
        }

        double getMultiplier() {
            return multiplier;
        }

    };

    private static final int NUM_SEGMENTS = 2;

    private Circle dashedCircle;
    private ParallelTransition compositeAnimation;

    public DashedCircleAnimation(Sprite sprite, double dashedCircleRadius, RotateDirection rotateDirection) {
        super(sprite);

        dashedCircle = new Circle(dashedCircleRadius);
        dashedCircle.getStrokeDashArray().setAll(0.75 * (1. / NUM_SEGMENTS) * 2 * Math.PI * dashedCircleRadius, 0.25 * (1. / NUM_SEGMENTS) * 2 * Math.PI * dashedCircleRadius);
        dashedCircle.setStrokeLineCap(StrokeLineCap.ROUND);
        dashedCircle.setFill(null);
        dashedCircle.setStrokeWidth(5);
        dashedCircle.setStroke(Color.YELLOW);

        final Timeline changingRadiusAnimation = new Timeline();

        KeyValue startRadiusKeyValue = new KeyValue(dashedCircle.radiusProperty(), dashedCircleRadius);
        KeyValue startStrokeColorKeyValue = new KeyValue(dashedCircle.strokeProperty(), Color.YELLOW);
        KeyFrame startKeyFrame = new KeyFrame(Duration.seconds(0.), startRadiusKeyValue, startStrokeColorKeyValue);

        KeyValue endRadiusKeyValue = new KeyValue(dashedCircle.radiusProperty(), 1.1 * dashedCircleRadius, Interpolator.LINEAR);
        KeyValue endStrokeColorKeyValue = new KeyValue(dashedCircle.strokeProperty(), Color.DARKORANGE, Interpolator.LINEAR);
        KeyFrame endKeyFrame = new KeyFrame(Duration.seconds(0.4), endRadiusKeyValue, endStrokeColorKeyValue);

        changingRadiusAnimation.getKeyFrames().addAll(startKeyFrame, endKeyFrame);
        changingRadiusAnimation.setAutoReverse(true);
        changingRadiusAnimation.setCycleCount(Animation.INDEFINITE);

        final RotateTransition rotateAnimation = new RotateTransition(Duration.seconds(NUM_SEGMENTS * 0.4), dashedCircle);
        rotateAnimation.setFromAngle(0.);
        rotateAnimation.setByAngle(rotateDirection.getMultiplier() * 360.);
        rotateAnimation.setInterpolator(Interpolator.LINEAR);
        rotateAnimation.setCycleCount(Animation.INDEFINITE);

        compositeAnimation = new ParallelTransition();
        compositeAnimation.getChildren().addAll(changingRadiusAnimation, rotateAnimation);
    }

    @Override
    public void start() {
        sprite.getParent().toFront();
        if (compositeAnimation.getStatus() == javafx.animation.Animation.Status.RUNNING) compositeAnimation.stop();
        if (!sprite.getChildren().contains(dashedCircle)) sprite.getChildren().add(dashedCircle);
        compositeAnimation.play();
    }

    @Override
    public void stop() {
        compositeAnimation.stop();
        sprite.getChildren().remove(dashedCircle);
    }

}