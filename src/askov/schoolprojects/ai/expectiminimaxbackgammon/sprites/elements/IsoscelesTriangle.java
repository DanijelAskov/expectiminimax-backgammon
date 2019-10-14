/*
 * Copyright (c) 2017, Danijel Askov
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.elements;

import javafx.scene.shape.Polygon;

/**
 * @author  Danijel Askov
 */
public class IsoscelesTriangle extends Polygon {

    public IsoscelesTriangle(double base, double height) {
        base = Math.abs(base);
        height = Math.abs(height);
        super.getPoints().setAll(0d, 0d, base / 2, height, base, 0d);
        super.setStrokeMiterLimit(Double.MAX_VALUE);
    }

}