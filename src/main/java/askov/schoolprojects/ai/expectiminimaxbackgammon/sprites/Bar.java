/*
 * Copyright (c) 2017, Danijel Askov
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.sprites;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * @author  Danijel Askov
 */
public class Bar extends CheckerStack {

    private final Text text = new Text("");
    
    public Bar() {
        text.setFont(Font.font("Cambria", FontWeight.BOLD, 28));
        text.setFill(Color.LIGHTSALMON);
        text.setStroke(Color.RED);
        text.setOpacity(0.50);
    }
    
    @Override
    public void update() {
        super.getChildren().clear();
        int numCheckers = checkers.size();
        for (Checker checker : checkers) {
            checker.setTranslateX(0);
            checker.setTranslateY(0);
            super.getChildren().add(checker);
        }
        if (numCheckers > 0) {
            text.setText(String.valueOf(numCheckers));
            super.getChildren().add(text);
            text.setTranslateX(checkers.get(0).getSize() / 4);
            text.setTranslateY(-checkers.get(0).getSize() / 4);
        }
    }    

    @Override
    public void animateSelected(boolean selected) {}
    
}