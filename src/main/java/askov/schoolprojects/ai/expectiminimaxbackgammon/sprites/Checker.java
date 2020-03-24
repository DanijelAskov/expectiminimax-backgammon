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
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.animation.DashedCircleAnimation;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.animation.ScalingAnimation;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

/**
 * @author Danijel Askov
 */
public class Checker extends Sprite {

    public enum CheckerColor {
        BLACK(0, "BLACK"),
        WHITE(1, "WHITE");

        private final int value;
        private final String description;

        CheckerColor(int value, String description) {
            this.value = value;
            this.description = description;
        }

        int getValue() {
            return value;
        }
        
        public static CheckerColor getOppositeCheckerColor(CheckerColor checkerColor) {
            switch (checkerColor) {
                case WHITE:
                    return BLACK;
                case BLACK:
                    return WHITE;
            }
            return WHITE;
        }
        
        @Override
        public String toString() {
            return description;
        }
    }
 
    private static final LinearGradient[] INNER_LIN_GRAD = {
        new LinearGradient(0.10, 0.20, 1, 0.25, true, CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(8, 8, 8)), new Stop(1, Color.rgb(77, 77, 77))), new LinearGradient(0.50, 0.50, 0.85, 0.85, true, CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(222, 219, 198)), new Stop(1, Color.rgb(251, 249, 245))),};
    private static final LinearGradient[] OUTER_LIN_GRAD = {
        new LinearGradient(0, 0.85, 1.1, 0.60, true, CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(74, 74, 74)), new Stop(1, Color.rgb(8, 8, 8))), new LinearGradient(0, 0.45, 1, 0.40, true, CycleMethod.NO_CYCLE, new Stop(0, Color.rgb(222, 220, 174)), new Stop(1, Color.rgb(183, 180, 165))),};

    private final CheckerColor checkerColor;
    private final Circle outerCircle;

    private final Shape crossedLinesMarker;

    private Animation animationMarked;
    private Animation animationSelected;

    public Checker(double size, CheckerColor checkerColor) {
        this.checkerColor = checkerColor;

        outerCircle = new Circle(size / 2);
        Circle innerCircle = new Circle(0.90 * (size / 2));

        innerCircle.setFill(INNER_LIN_GRAD[checkerColor.getValue()]);
        outerCircle.setFill(OUTER_LIN_GRAD[checkerColor.getValue()]);

        outerCircle.setStroke(null);
        innerCircle.setStroke(null);

        super.getChildren().setAll(outerCircle, innerCircle);

        animationSelected = new DashedCircleAnimation(this, 1.2 * outerCircle.getRadius(), DashedCircleAnimation.RotateDirection.CLOCKWISE);

        double r = innerCircle.getRadius();
        Line line1 = new Line(-0.50 * r, -0.50 * r, 0.50 * r, 0.50 * r);
        line1.setStrokeWidth(0.40 * r);

        Line line2 = new Line(-0.50 * r, 0.50 * r, 0.50 * r, -0.50 * r);
        line2.setStrokeWidth(0.40 * r);

        crossedLinesMarker = Shape.union(line1, line2);
        crossedLinesMarker.setStroke(Color.DARKRED);
        crossedLinesMarker.setFill(Color.RED);
        crossedLinesMarker.setOpacity(0.5);

        animationMarked = new ScalingAnimation(crossedLinesMarker);
    }

    public double getSize() {
        return outerCircle.getRadius() * 2;
    }

    public CheckerColor getCheckerColor() {
        return checkerColor;
    }

    public void animateSelected(boolean selected) {
        if (selected) animationSelected.start(); else animationSelected.stop();
    }

    public void animateBlot(boolean isBlot) {
        if (isBlot) {
            if (!getChildren().contains(crossedLinesMarker)) getChildren().add(crossedLinesMarker);
            animationMarked.start();
        } else {
            animationMarked.stop();
            getChildren().remove(crossedLinesMarker);
        }
    }

    @Override
    public void update() {

    }

    @Override
    public String toString() {
        return checkerColor.toString() + " checker";
    }

}