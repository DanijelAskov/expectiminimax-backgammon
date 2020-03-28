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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author  Danijel Askov
 */
public class Move {
    
    private final List<CheckerRelocation> checkerRelocations = new ArrayList<>();
    
    public Move() {
        
    }
    
    public void addCheckerRelocation(CheckerRelocation checkerRelocation) {
        checkerRelocations.add(checkerRelocation);
    }
    
    public void addCheckerRelocations(CheckerRelocation... checkerRelocations) {
        this.checkerRelocations.addAll(Arrays.asList(checkerRelocations));
    }
    
    public List<CheckerRelocation> getCheckerRelocations() {
        return checkerRelocations;
    }
    
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Move: (");
        for (int i = 0; i < checkerRelocations.size(); i++) {
            stringBuilder.append(checkerRelocations.get(i).toString());
            if (i < checkerRelocations.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
    
    @Override
    public boolean equals(Object object) { 
        if (object == this) { 
            return true; 
        } 
        
        if (!(object instanceof Move)) { 
            return false; 
        } 
          
        Move move = (Move) object; 
        
        if (this.checkerRelocations.size() != move.checkerRelocations.size())
            return false;
        
        if (this.checkerRelocations.isEmpty())
            return true;
        
        CheckerRelocation firstCheckerRelocation1 = this.getCheckerRelocations().get(0);
        CheckerRelocation firstCheckerRelocation2 = move.getCheckerRelocations().get(0);
        
        CheckerRelocation lastCheckerRelocation1 = this.getCheckerRelocations().get(this.getCheckerRelocations().size() - 1);
        CheckerRelocation lastCheckerRelocation2 = move.getCheckerRelocations().get(move.getCheckerRelocations().size() - 1);
        
        return firstCheckerRelocation1.getSourceCheckerStack()== firstCheckerRelocation2.getSourceCheckerStack() && lastCheckerRelocation1.getDestinationCheckerStack() == lastCheckerRelocation2.getDestinationCheckerStack(); 
    }
    
}