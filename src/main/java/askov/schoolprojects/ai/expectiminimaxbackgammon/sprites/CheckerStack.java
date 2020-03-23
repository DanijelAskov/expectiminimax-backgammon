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

import java.util.EmptyStackException;
import java.util.Stack;
import javafx.scene.shape.Shape;

/**
 * @author  Danijel Askov
 */
public abstract class CheckerStack extends Sprite {
    
    protected Shape backgroundTriangle;
    protected final Stack<Checker> checkers = new Stack<>();
    
    public void pushChecker(Checker checker) {
        checkers.push(checker);
    }
    
    public Checker popChecker() {
        return checkers.pop();
    }
    
    public Checker peekChecker() {
        try {
            return checkers.peek();
        } catch (EmptyStackException e) {
            return null;
        }
    }
    
    public int getNumCheckers() {
        return checkers.size();
    }
    
    public boolean isEmpty() {
        return checkers.isEmpty();
    }
    
    public abstract void animateSelected(boolean selected);

    @Override
    public abstract void update();
    
}