/*
 * Copyright (c) 2017, Danijel Askov
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.sprites;

import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.elements.IsoscelesTriangle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * @author  Danijel Askov
 */
public class Point extends CheckerStack {

    public enum PointColor {

        FIRST(Color.web("0xDA221D")),
        SECOND(Color.web("0xDEDCAE"));

        private final Color color;

        PointColor(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }

    }
    
    private static final int MAX_HEIGHT = 5;

    private Timeline animation;
    
    public Point(double width, double height, PointColor color) {
        background = new IsoscelesTriangle(width, height);
        background.setFill(color.getColor());
        background.setStrokeWidth(1);
        background.setStroke(Color.BLACK);
        super.getChildren().setAll(background);
    }
    
    @Override
    public void animateSelected(boolean selected) {
        if (animation != null)
                animation.stop();
        if(selected) {     
            if (animation == null) {
                animation = new Timeline();         
               
                KeyValue startStrokeWidthKeyValue = new KeyValue(background.strokeWidthProperty(), 0);
                KeyValue startStrokeColorKeyValue = new KeyValue(background.strokeProperty(), Color.BLACK);
                KeyFrame startKeyFrame = new KeyFrame(Duration.seconds(0), startStrokeWidthKeyValue, startStrokeColorKeyValue);

                KeyValue endStrokeWidthKeyValue = new KeyValue(background.strokeWidthProperty(), 4);
                KeyValue endStrokeColorKeyValue = new KeyValue(background.strokeProperty(), Color.YELLOW);
                KeyFrame endKeyFrame = new KeyFrame(Duration.seconds(0.40), endStrokeColorKeyValue, endStrokeWidthKeyValue);

                animation.getKeyFrames().addAll(startKeyFrame, endKeyFrame);
                
                animation.setAutoReverse(true);
                animation.setCycleCount(Animation.INDEFINITE);
            }
            
            animation.play();
        } else { 
            background.setStrokeWidth(1);
            background.setStroke(Color.BLACK);
        }
    }

    @Override
    public void update() {
        super.getChildren().clear();
        super.getChildren().add(background);
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