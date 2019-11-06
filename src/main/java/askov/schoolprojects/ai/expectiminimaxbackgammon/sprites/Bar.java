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