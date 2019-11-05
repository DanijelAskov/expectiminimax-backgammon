/*
 * Copyright (c) 2017, Danijel Askov
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.sprites;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * @author  Danijel Askov
 */
public class Die extends Sprite {
    
    private static final double RELATIVE_STROKE_WIDTH = 0.05;
    private static final int[][] VALUE_PATTERNS = {
        {5},
        {1, 9},
        {1, 5, 9},
        {1, 3, 7, 9},
        {1, 3, 5, 7, 9},
        {1, 2, 3, 7, 8, 9},
    };

    public static final Die[] DICE_PAIRS = new Die[] {
        new Die(1), new Die(1),
        new Die(1), new Die(2),
        new Die(1), new Die(3),
        new Die(1), new Die(4),
        new Die(1), new Die(5),
        new Die(1), new Die(6),
        new Die(2), new Die(2),
        new Die(2), new Die(3),
        new Die(2), new Die(4),
        new Die(2), new Die(5),
        new Die(2), new Die(6),
        new Die(3), new Die(3),
        new Die(3), new Die(4),
        new Die(3), new Die(5),
        new Die(3), new Die(6),
        new Die(4), new Die(4),
        new Die(4), new Die(5),
        new Die(4), new Die(6),
        new Die(5), new Die(5),
        new Die(5), new Die(6),
        new Die(6), new Die(6)
    };

    private final Circle[] dots = new Circle[9];
    
    private final double size;
    
    private int value;
    private boolean used;
    
    public Die(double size)  {
        Rectangle rectangle = new Rectangle();
        rectangle.setWidth(this.size = size);
        rectangle.setHeight(size);
        rectangle.setArcHeight(size / 4);
        rectangle.setArcWidth(size / 4);
        rectangle.setFill(Color.rgb(238, 7, 1));
        rectangle.setStroke(Color.BLACK);
        rectangle.setStrokeWidth(RELATIVE_STROKE_WIDTH * size);
        super.getChildren().add(rectangle);
        
        for (int i = 0; i < 9; i++) {
            dots[i] = new Circle(0.10 * size);
            dots[i].setTranslateX(0.25 * size + (i % 3) * 0.25 * size);
            dots[i].setTranslateY((i < 3 ? 0.25 : i < 6 ? 0.50 : 0.75) * size);
            dots[i].setFill(Color.WHITE);
            dots[i].setStroke(null);
            dots[i].setVisible(false);
            
            super.getChildren().add(dots[i]);
        }
    }

    public Die(double size, int value)  {
        this(size);
        try {
            setValue(value);
        } catch (DieValueOutOfBoundsException ex) {
            Logger.getLogger(Die.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Die(int value)  {
        this(0, value);
    }
    
    public void roll() {
        try {
            setValue(value = (int) (Math.random() * 6) + 1);
        } catch (DieValueOutOfBoundsException ex) {
            Logger.getLogger(Die.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void chooseWhichPointsToShow(int... indices) {
        for (int index : indices) {
            dots[index - 1].setVisible(true);
        }
    }
    
    public final void setValue(int value) throws DieValueOutOfBoundsException {
        if (value < 1 || value > 6) {
            throw new DieValueOutOfBoundsException();
        }     
        this.value = value;
    }
    
    public void setUsed(boolean used) {
        this.used = used;
    }
    
    public boolean isUsed() {
        return used;
    }
    
    public int getValue() {
        return value;
    }
    
    @Override
    public void update() {
        for (Circle dot : dots) {
            dot.setVisible(false);
        }
        chooseWhichPointsToShow(VALUE_PATTERNS[value - 1]);
    }
    
    @Override
    public String toString() {
        return "die " + value  + "";
    }
    
    private FadeTransition fadeOutTransition;
    private FadeTransition fadeInTransition;

    public void hide() {
        if (fadeOutTransition == null) {
            fadeOutTransition = new FadeTransition(Duration.seconds(1), this);
            fadeOutTransition.setFromValue(1);
            fadeOutTransition.setToValue(0);
        }
        fadeOutTransition.play();
    }

    public void show() {
        if (fadeInTransition == null) {
            fadeInTransition = new FadeTransition(Duration.seconds(1), this);
            fadeInTransition.setFromValue(0);
            fadeInTransition.setToValue(1);
        }
        fadeInTransition.play();
    }
    
    public double getSize() {
        return size;
    }

    public static double probability(Die die1, Die die2) {
        return die1.value == die2.value ? 1 / 36. : 1 / 18.;
    }
    
}