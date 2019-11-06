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

import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

/**
 * @author  Danijel Askov
 */
public class BoardSide extends Sprite {

    private static final Color SIDE_FILL = Color.web("0x164623");
    public static final int NUM_POINTS_PER_SIDE = 12;
    private static final double REL_POINT_HEIGHT = 0.45;

    private final double width;

    private final List<Point> points = new ArrayList<>();

    public BoardSide(double width, double height) {
        Rectangle background = new Rectangle();
        background.setWidth(this.width = width);
        background.setHeight(height);
        background.setFill(SIDE_FILL);
        background.setStroke(Color.BLACK);
        background.setStrokeWidth(1);
        super.getChildren().add(background);

        for (int i = 0; i < NUM_POINTS_PER_SIDE; i++) {
            Point point = new Point(0.95 * width / (NUM_POINTS_PER_SIDE / 2.), REL_POINT_HEIGHT * height, Point.PointColor.values()[i % 2]);
            if (i >= NUM_POINTS_PER_SIDE / 2) {
                point.setTranslateX(0.025 * width + (i - NUM_POINTS_PER_SIDE / 2.) * (0.95 * width / (NUM_POINTS_PER_SIDE / 2.)));
            } else {
                point.setTranslateX(0.025 * width + (NUM_POINTS_PER_SIDE / 2. - 1 - i) * (0.95 * width / (NUM_POINTS_PER_SIDE / 2.)));
                point.setTranslateY(height);
                point.getTransforms().addAll(
                        new Scale(1, -1, point.getTranslateX() + (0.95 * width / (NUM_POINTS_PER_SIDE / 2.)) / 2, 0)
                );
            }
            points.add(point);
            super.getChildren().add(point);
        }
    }

    public Point getPoint(int pointIndex) throws CheckerStackIndexOutOfBoundsException {
        if (pointIndex < 1 || pointIndex > NUM_POINTS_PER_SIDE) {
            throw new CheckerStackIndexOutOfBoundsException();
        }
        return points.get(pointIndex - 1);
    }

    public Checker pushChecker(Checker.CheckerColor checkerColor, int pointIndex) throws CheckerStackIndexOutOfBoundsException {
        Checker checker;
        getPoint(pointIndex).pushChecker(checker = new Checker(0.95 * width / (NUM_POINTS_PER_SIDE / 2.), checkerColor));
        return checker;
    }
    
    public Checker peekChecker(int pointIndex) throws CheckerStackIndexOutOfBoundsException {
        return getPoint(pointIndex).peekChecker();
    }
    
    public Checker popChecker(int pointIndex) throws CheckerStackIndexOutOfBoundsException {
        return getPoint(pointIndex).popChecker();
    }

    @Override
    public void update() {
        for (Point point : points) {
            point.update();
        }
    }

}
