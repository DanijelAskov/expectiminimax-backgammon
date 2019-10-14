/*
 * Copyright (c) 2017, Danijel Askov
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.sprites;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

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

    private final Shape x;

    private ScaleTransition blotAnimation;

    public Checker(double size, CheckerColor checkerColor) {
        this.checkerColor = checkerColor;

        outerCircle = new Circle(size / 2);
        Circle innerCircle = new Circle(0.90 * (size / 2));

        innerCircle.setFill(INNER_LIN_GRAD[checkerColor.getValue()]);
        outerCircle.setFill(OUTER_LIN_GRAD[checkerColor.getValue()]);

        outerCircle.setStroke(null);
        innerCircle.setStroke(null);

        super.getChildren().setAll(outerCircle, innerCircle);

        double r = innerCircle.getRadius();
        Line l1 = new Line(-0.50 * r, -0.50 * r, 0.50 * r, 0.50 * r);
        l1.setStrokeWidth(0.40 * r);

        Line l2 = new Line(-0.50 * r, 0.50 * r, 0.50 * r, -0.50 * r);
        l2.setStrokeWidth(0.40 * r);

        x = Shape.union(l1, l2);
        x.setStroke(Color.DARKRED);
        x.setFill(Color.RED);
        x.setOpacity(0.5);
    }

    public double getSize() {
        return outerCircle.getRadius() * 2;
    }

    public CheckerColor getCheckerColor() {
        return checkerColor;
    }

    private Timeline selectedAnimation;

    public void animateSelected(boolean selected) {
        if (selected) {
            if (selectedAnimation == null) {
                selectedAnimation = new Timeline();

                KeyValue startStrokeWidthKeyValue = new KeyValue(outerCircle.strokeWidthProperty(), 0);
                KeyValue startStrokeColorKeyValue = new KeyValue(outerCircle.strokeProperty(), Color.BLACK);
                KeyFrame startKeyFrame = new KeyFrame(Duration.seconds(0), startStrokeWidthKeyValue, startStrokeColorKeyValue);

                KeyValue endStrokeWidthKeyValue = new KeyValue(outerCircle.strokeWidthProperty(), 4);
                KeyValue endStrokeColorKeyValue = new KeyValue(outerCircle.strokeProperty(), Color.YELLOW);
                KeyFrame endKeyFrame = new KeyFrame(Duration.seconds(0.40), endStrokeColorKeyValue, endStrokeWidthKeyValue);

                selectedAnimation.getKeyFrames().addAll(startKeyFrame, endKeyFrame);

                selectedAnimation.setAutoReverse(true);
                selectedAnimation.setCycleCount(Animation.INDEFINITE);
            }

            selectedAnimation.play();
        } else {
            if (selectedAnimation != null && selectedAnimation.getStatus() == Animation.Status.RUNNING) {
                selectedAnimation.stop();
            }
            outerCircle.setStrokeWidth(0);
            outerCircle.setStroke(Color.BLACK);
        }
    }

    public void animateBlot(boolean isBlot) {
        if (blotAnimation != null) {
            blotAnimation.stop();
        }

        if (isBlot) {
            if (blotAnimation == null) {
                blotAnimation = new ScaleTransition(Duration.seconds(0.40), x);
                blotAnimation.setFromX(1);
                blotAnimation.setToX(1.5);
                blotAnimation.setFromY(1);
                blotAnimation.setToY(1.5);
                blotAnimation.setAutoReverse(true);
                blotAnimation.setCycleCount(Animation.INDEFINITE);
            }
            if (!getChildren().contains(x)) {
                getChildren().add(x);
            }
            blotAnimation.play();
        } else {
            getChildren().remove(x);
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