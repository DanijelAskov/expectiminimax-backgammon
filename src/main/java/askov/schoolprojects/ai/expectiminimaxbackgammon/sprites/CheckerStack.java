/*
 * Copyright (c) 2017, Danijel Askov
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.sprites;

import java.util.EmptyStackException;
import java.util.Stack;
import javafx.scene.shape.Shape;

/**
 * @author  Danijel Askov
 */
public abstract class CheckerStack extends Sprite {
    
    protected Shape background;
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