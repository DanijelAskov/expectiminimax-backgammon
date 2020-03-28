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

package askov.schoolprojects.ai.expectiminimaxbackgammon.gameplay.player.move;

import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Board;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Checker;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.CheckerStack;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Die;

/**
 * @author  Danijel Askov
 */
public class CheckerRelocation {
    
    private final CheckerStack sourceCheckerStack;
    private final CheckerStack destinationCheckerStack;
    private final Checker checker;
    private final Die die;
    private final boolean removesBlot;
    
    private int sourcePointIndex;
    private int destinationPointIndex;

    public CheckerRelocation(CheckerStack sourceCheckerStack, CheckerStack destinationCheckerStack, Checker checker, Die die) {
        this.sourceCheckerStack = sourceCheckerStack;
        this.destinationCheckerStack = destinationCheckerStack;
        this.die = die;
        this.checker = checker;
        Checker destinationCheckerStackTopChecker = destinationCheckerStack.peekChecker();
        this.removesBlot = destinationCheckerStackTopChecker != null && (destinationCheckerStackTopChecker.getCheckerColor() != checker.getCheckerColor() && destinationCheckerStack.getNumCheckers() < 2);
    }
    
    public CheckerRelocation(CheckerStack sourceCheckerStack, CheckerStack destinationCheckerStack, Checker checker) {
        this(sourceCheckerStack, destinationCheckerStack, checker, null);
    } 
    
    public void relocateForward() {
        destinationCheckerStack.pushChecker(sourceCheckerStack.popChecker());
        die.setUsed(true);
    }
    
    public void relocateBackward() {
        sourceCheckerStack.pushChecker(destinationCheckerStack.popChecker());
        die.setUsed(false);
    }
    
    public CheckerStack getSourceCheckerStack() {
        return sourceCheckerStack;
    }
    
    public CheckerStack getDestinationCheckerStack() {
        return destinationCheckerStack;
    }
    
    public Checker getChecker() {
        return checker;
    }
    
    public void setSourcePointIndex(int pointIndex) {
        sourcePointIndex = pointIndex;
    }
    
    public void setDestinationPointIndex(int pointIndex) {
        destinationPointIndex = pointIndex;
    }
    
    public Die getDie() {
        return die;
    }
    
    public boolean removesBlot() {
        return removesBlot;
    }
    
    @Override
    public String toString() {
        String source = sourcePointIndex == Board.NUM_POINTS + 1 ? String.format("  %-6s", "Bar") : (sourcePointIndex == 0 ? String.format("%8s", "Home") : String.format("Point %02d", sourcePointIndex));
        String destination = destinationPointIndex == Board.NUM_POINTS + 1 ? String.format("%8s", "Bar") : ((destinationPointIndex == 0 ? String.format("  %-6s", "Home") : String.format("Point %02d", destinationPointIndex)) + (removesBlot ? "*" : "^"));
        String arrow = " --[top " + checker + " using " + die.toString() + "]--> ";
        return source + arrow + destination;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object == this) { 
            return true; 
        } 
        
        if (!(object instanceof CheckerRelocation)) { 
            return false; 
        } 
          
        CheckerRelocation checkerRelocation = (CheckerRelocation) object; 
        
        return this.sourceCheckerStack == checkerRelocation.sourceCheckerStack && this.destinationCheckerStack == checkerRelocation.destinationCheckerStack && this.checker == checkerRelocation.checker;
    }
    
}