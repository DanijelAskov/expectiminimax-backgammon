/*
 * Copyright (c) 2017, Danijel Askov
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.sprites;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author  Danijel Askov
 */
public class Home extends CheckerStack {
    
    public enum StackingDirection {
        TOP_DOWN,
        BOTTOM_UP,
    }

    private static final LinearGradient[] LIN_GRAD = {
            new LinearGradient(0, 0, 0.5, 0, true, CycleMethod.REFLECT, new Stop(0, Color.rgb(8, 8, 8)), new Stop(1, Color.rgb(74, 74, 74))), new LinearGradient(0, 0, 0.5, 0, true, CycleMethod.REFLECT, new Stop(0, Color.rgb(183, 180, 165)), new Stop(1, Color.rgb(222, 220, 174))),
    };

    private final double width, height;
    private final StackingDirection direction;

    private Timeline animation;

    public Home(double width, double height, StackingDirection direction) {
        background = new Rectangle(this.width = width, this.height = height);
        background.setFill(Board.BOARD_FILL.brighter());
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(1);
        this.direction = direction;
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
        int i = 0;
        for (Checker checker : checkers) {
            Rectangle rectangle = new Rectangle(width - 4, (1. / 15) * height, LIN_GRAD[checker.getCheckerColor().getValue()]);
            rectangle.setStroke(Color.BLACK);
            rectangle.setStrokeWidth(0.75);
            rectangle.setTranslateX(2);
            rectangle.setTranslateY(direction == StackingDirection.TOP_DOWN ? i++ * (1. / 15) * height : height - i++ * (1. / 15) * height - (1. / 15) * height);
            rectangle.setArcWidth(0.20 * width);
            rectangle.setArcHeight(0.50 * height);
            super.getChildren().add(rectangle);
        }
    }

}