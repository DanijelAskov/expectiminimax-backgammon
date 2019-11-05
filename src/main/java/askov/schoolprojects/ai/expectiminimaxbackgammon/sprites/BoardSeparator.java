/*
 * Copyright (c) 2017, Danijel Askov
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.sprites;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

/**
 * @author  Danijel Askov
 */
public class BoardSeparator extends Sprite {

    private static class Hinge extends Group {

        static final double RELATIVE_HINGE_HEIGHT = 0.05;

        private final double width;
        private final double height;

        public Hinge(double height) {
            this.height = height;
            width = height / 2;

            Rectangle outerRectangle = new Rectangle();
            outerRectangle.setHeight(height);
            outerRectangle.setWidth(width);
            outerRectangle.setFill(Color.rgb(93, 61, 33));

            Rectangle innerRectangle = new Rectangle();
            innerRectangle.setHeight(height);
            innerRectangle.setWidth(0.70 * width);
            innerRectangle.setTranslateX(0.15 * width);
            innerRectangle.setFill(new LinearGradient(0, 0, 0.5, 0, true, CycleMethod.REFLECT, new Stop(0, Color.rgb(161, 132, 69)), new Stop(1, Color.rgb(228, 224, 190))));

            Line topCenterLine = new Line();
            topCenterLine.setStartX(0.15 * width);
            topCenterLine.setStartY(height / 2 - 0.15 * width / 2);
            topCenterLine.setEndX(0.85 * width);
            topCenterLine.setEndY(height / 2 - 0.15 * width / 2);
            topCenterLine.setStrokeWidth(0.15 * width);
            topCenterLine.setStroke(Color.rgb(177, 135, 90));
            topCenterLine.setStrokeLineCap(StrokeLineCap.BUTT);

            Line bottomCenterLine = new Line();
            bottomCenterLine.setStartX(0.15 * width);
            bottomCenterLine.setStartY(height / 2 + 0.15 * width / 2);
            bottomCenterLine.setEndX(0.85 * width);
            bottomCenterLine.setEndY(height / 2 + 0.15 * width / 2);
            bottomCenterLine.setStrokeWidth(0.15 * width);
            bottomCenterLine.setStroke(Color.rgb(119, 75, 31));
            bottomCenterLine.setStrokeLineCap(StrokeLineCap.BUTT);

            super.getChildren().setAll(outerRectangle, innerRectangle, topCenterLine, bottomCenterLine);
        }
        
        public double getWidth() {
            return width;
        }
        
        public double getHeight() {
            return height;
        }

    }

    public BoardSeparator(double width, double height) {
        Line leftLine = new Line();
        leftLine.setStartX(0);
        leftLine.setStartY(0);
        leftLine.setEndX(0);
        leftLine.setEndY(height);
        leftLine.setStrokeWidth(width / 2);
        leftLine.setStroke(Color.rgb(119, 75, 31));

        Line rightLine = new Line();
        rightLine.setStartX(0);
        rightLine.setStartY(0);
        rightLine.setEndX(0);
        rightLine.setEndY(height);
        rightLine.setTranslateX(width / 2);
        rightLine.setStrokeWidth(width / 2);
        rightLine.setStroke(Color.rgb(177, 135, 90));

        Hinge topHinge = new Hinge(Hinge.RELATIVE_HINGE_HEIGHT * height);
        topHinge.setTranslateX(-(topHinge.getWidth() / 2 - width / 4));
        topHinge.setTranslateY(0.10 * height);

        Hinge bottomHinge = new Hinge(Hinge.RELATIVE_HINGE_HEIGHT * height);
        bottomHinge.setTranslateX(-(bottomHinge.getWidth() / 2 - width / 4));
        bottomHinge.setTranslateY(0.90 * height - bottomHinge.getHeight());

        super.getChildren().setAll(leftLine, rightLine, topHinge, bottomHinge);
    }
    
    @Override
    public void update() {}

}