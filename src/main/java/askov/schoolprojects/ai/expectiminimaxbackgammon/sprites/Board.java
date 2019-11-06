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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * @author  Danijel Askov
 */
public class Board extends Sprite {

    public static final Color BOARD_FILL = Color.web("0x795731");

    private static final double RELATIVE_EDGE_THICKNESS = 0.05;
    public static final int NUM_POINTS = 2 * BoardSide.NUM_POINTS_PER_SIDE;
    private static final double RELATIVE_SEPARATOR_WIDTH = 0.005;
    private static final double RELATIVE_SEPARATOR_HEIGHT = 0.95;

    private final Rectangle background = new Rectangle();
    private final BoardSide leftSide, rightSide;
    private final BoardSeparator boardSeparator;
    private final Home whiteHome, blackHome;
    private final Bar whiteBar, blackBar;

    private final double width;
    private final double height;

    public Board(double width, double height) {
        background.setWidth(this.width = width);
        background.setHeight(this.height = height);
        background.setFill(BOARD_FILL);
        super.getChildren().add(background);

        double sideWidth = 0.45 * (width - 4 * RELATIVE_EDGE_THICKNESS * width);
        double sideHeight = height - 2 * RELATIVE_EDGE_THICKNESS * height;

        leftSide = new BoardSide(sideWidth, sideHeight);
        leftSide.setTranslateX(1.8 * RELATIVE_EDGE_THICKNESS * width);
        leftSide.setTranslateY(RELATIVE_EDGE_THICKNESS * height);

        rightSide = new BoardSide(sideWidth, sideHeight);
        rightSide.setTranslateX(width - sideWidth - 1.8 * RELATIVE_EDGE_THICKNESS * width);
        rightSide.setTranslateY(RELATIVE_EDGE_THICKNESS * height);

        super.getChildren().addAll(leftSide, rightSide);
        
        for (int i = 1; i <= NUM_POINTS; i++) {
            Text text;
            text = new Text(String.valueOf(i));
            text.setFont(Font.font("Cambria", FontWeight.BOLD, 14));
            text.setFill(Color.WHEAT);
            if (i > NUM_POINTS / 2 && i <= 3 * NUM_POINTS / 4) {
                text.setX(leftSide.getTranslateX() + 0.025 * sideWidth + 0.35 * 0.95 * sideWidth / (NUM_POINTS / 4.) + ((i - 1) - NUM_POINTS / 2.) * 0.95 * sideWidth / (NUM_POINTS / 4.));
                text.setY(0.70 * RELATIVE_EDGE_THICKNESS * height);
            } else if (i > 3 * NUM_POINTS / 4) {
                text.setX(rightSide.getTranslateX() + 0.025 * sideWidth + 0.35 * 0.95 * sideWidth / (NUM_POINTS / 4.) + ((i - 1) - 3 * NUM_POINTS / 4.) * 0.95 * sideWidth / (NUM_POINTS / 4.));
                text.setY(0.70 * RELATIVE_EDGE_THICKNESS * height);
            } else if (i > NUM_POINTS / 4) {
                text.setX(leftSide.getTranslateX() + sideWidth - 0.025 * sideWidth - 0.60 * 0.95 * sideWidth / (NUM_POINTS / 4.) - ((i - 1) - NUM_POINTS / 4.) * 0.95 * sideWidth / (NUM_POINTS / 4.));
                text.setY(RELATIVE_EDGE_THICKNESS * height + sideHeight + 0.70 * RELATIVE_EDGE_THICKNESS * height);
            } else {
                text.setX(rightSide.getTranslateX() + sideWidth - 0.025 * sideWidth - 0.60 * 0.95 * sideWidth / (NUM_POINTS / 4.) - (i - 1) * 0.95 * sideWidth / (NUM_POINTS / 4.));
                text.setY(RELATIVE_EDGE_THICKNESS * height + sideHeight + 0.70 * RELATIVE_EDGE_THICKNESS * height);
            }
            super.getChildren().add(text);
        }

        boardSeparator = new BoardSeparator(RELATIVE_SEPARATOR_WIDTH * width, RELATIVE_SEPARATOR_HEIGHT * height);
        boardSeparator.setTranslateX(width / 2 - RELATIVE_SEPARATOR_WIDTH * width / 2);
        boardSeparator.setTranslateY((height - RELATIVE_SEPARATOR_HEIGHT * height) / 2);
        super.getChildren().add(boardSeparator);
        
        double homeWidth = 0.70 * (width - (rightSide.getTranslateX() + sideWidth));
        double homeHeight = height / 4;
        
        blackHome = new Home(homeWidth, homeHeight, Home.StackingDirection.TOP_DOWN);
        blackHome.setTranslateX(rightSide.getTranslateX() + sideWidth + 0.15 * (width - (rightSide.getTranslateX() + sideWidth)));
        blackHome.setTranslateY(RELATIVE_EDGE_THICKNESS * height);
        whiteHome = new Home(homeWidth, homeHeight, Home.StackingDirection.BOTTOM_UP);
        whiteHome.setTranslateX(rightSide.getTranslateX() + sideWidth + 0.15 * (width - (rightSide.getTranslateX() + sideWidth)));
        whiteHome.setTranslateY(height - RELATIVE_EDGE_THICKNESS * height - height / 4);
        super.getChildren().addAll(whiteHome, blackHome);
        
        whiteBar = new Bar();
        whiteBar.setTranslateX(width / 2);
        whiteBar.setTranslateY(2 * height / 3);
        blackBar = new Bar();
        blackBar.setTranslateX(width / 2);
        blackBar.setTranslateY(height / 3);
        
        super.getChildren().addAll(whiteBar, blackBar);
    }
    
    public Bar getBar(Checker.CheckerColor color) {
        switch (color) {
            case WHITE:
                return whiteBar;
            case BLACK:
                return blackBar;
        }
        return null;
    }
    
    public Home getHome(Checker.CheckerColor color) {
        switch (color) {
            case WHITE:
                return whiteHome;
            case BLACK:
                return blackHome;
        }
        return null;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }

    public final Checker createChecker(Checker.CheckerColor color, int pointIndex) throws CheckerStackIndexOutOfBoundsException {
        if (pointIndex < 1 || pointIndex > NUM_POINTS) {
            throw new CheckerStackIndexOutOfBoundsException();
        }
        switch (color) {
            case WHITE:
                if (pointIndex <= NUM_POINTS / 4) {
                    return rightSide.pushChecker(color, pointIndex);
                } else if (pointIndex >= 3 * NUM_POINTS / 4 + 1) {
                    return rightSide.pushChecker(color, pointIndex - NUM_POINTS / 2);
                } else {
                    return leftSide.pushChecker(color, pointIndex - NUM_POINTS / 4);
                }
            case BLACK:
                if (pointIndex <= NUM_POINTS / 4) {
                    return rightSide.pushChecker(color, NUM_POINTS / 2 - pointIndex + 1);
                } else if (pointIndex >= 3 * NUM_POINTS / 4 + 1) {
                    return rightSide.pushChecker(color, NUM_POINTS - pointIndex + 1);
                } else {
                    return leftSide.pushChecker(color, 3 * NUM_POINTS / 4 - pointIndex + 1);
                }
        }
        return null;
    }
    
    public final Checker peekChecker(Checker.CheckerColor color, int checkerStackIndex) throws CheckerStackIndexOutOfBoundsException {
        if (checkerStackIndex < 0 || checkerStackIndex > NUM_POINTS + 1) {
            throw new CheckerStackIndexOutOfBoundsException();
        }
        switch (color) {
            case WHITE:
                if (checkerStackIndex == 0) {
                    return whiteHome.peekChecker();
                } else if (checkerStackIndex == NUM_POINTS + 1) {
                    return whiteBar.peekChecker();
                } else if (checkerStackIndex <= NUM_POINTS / 4) {
                    return rightSide.peekChecker(checkerStackIndex);
                } else if (checkerStackIndex >= 3 * NUM_POINTS / 4 + 1) {
                    return rightSide.peekChecker(checkerStackIndex - NUM_POINTS / 2);
                } else {
                    return leftSide.peekChecker(checkerStackIndex - NUM_POINTS / 4);
                }
            case BLACK:
                if (checkerStackIndex == 0) {
                    return blackHome.peekChecker();
                } else if (checkerStackIndex == NUM_POINTS + 1) {
                    return blackBar.peekChecker();
                } else if (checkerStackIndex <= NUM_POINTS / 4) {
                    return rightSide.peekChecker(NUM_POINTS / 2 - checkerStackIndex + 1);
                } else if (checkerStackIndex >= 3 * NUM_POINTS / 4 + 1) {
                    return rightSide.peekChecker(NUM_POINTS - checkerStackIndex + 1);
                } else {
                    return leftSide.peekChecker(3 * NUM_POINTS / 4 - checkerStackIndex + 1);
                }
        }
        return null;
    }
    
    public final Checker popChecker(Checker.CheckerColor color, int checkerStackIndex) throws CheckerStackIndexOutOfBoundsException {
        if (checkerStackIndex < 1 || checkerStackIndex > NUM_POINTS + 1) {
            throw new CheckerStackIndexOutOfBoundsException();
        }
        switch (color) {
            case WHITE:
                if (checkerStackIndex == NUM_POINTS + 1) {
                    return whiteBar.popChecker();
                } else if (checkerStackIndex <= NUM_POINTS / 4) {
                    return rightSide.popChecker(checkerStackIndex);
                } else if (checkerStackIndex >= 3 * NUM_POINTS / 4 + 1) {
                    return rightSide.popChecker(checkerStackIndex - NUM_POINTS / 2);
                } else {
                    return leftSide.popChecker(checkerStackIndex - NUM_POINTS / 4);
                }
            case BLACK:
                if (checkerStackIndex == NUM_POINTS + 1) {
                    return blackBar.popChecker();
                } else if (checkerStackIndex <= NUM_POINTS / 4) {
                    return rightSide.popChecker(NUM_POINTS / 2 - checkerStackIndex + 1);
                } else if (checkerStackIndex >= 3 * NUM_POINTS / 4 + 1) {
                    return rightSide.popChecker(NUM_POINTS - checkerStackIndex + 1);
                } else {
                    return leftSide.popChecker(3 * NUM_POINTS / 4 - checkerStackIndex + 1);
                }
        }
        return null;
    }
    
    public CheckerStack getCheckerStack(Checker.CheckerColor color, int checkerStackIndex) throws CheckerStackIndexOutOfBoundsException {
        if (checkerStackIndex < 0 || checkerStackIndex > NUM_POINTS + 1) {
            throw new CheckerStackIndexOutOfBoundsException();
        }
        switch (color) {
            case WHITE:
                if (checkerStackIndex == 0) {
                    return whiteHome;
                } else if (checkerStackIndex == NUM_POINTS + 1) {
                    return whiteBar;
                } else if (checkerStackIndex <= NUM_POINTS / 4) {
                    return rightSide.getPoint(checkerStackIndex);
                } else if (checkerStackIndex >= 3 * NUM_POINTS / 4 + 1) {
                    return rightSide.getPoint(checkerStackIndex - NUM_POINTS / 2);
                } else {
                    return leftSide.getPoint(checkerStackIndex - NUM_POINTS / 4);
                }
            case BLACK:
                if (checkerStackIndex == 0) {
                    return blackHome;
                } else if (checkerStackIndex == NUM_POINTS + 1) {
                    return blackBar;
                } else if (checkerStackIndex <= NUM_POINTS / 4) {
                    return rightSide.getPoint(NUM_POINTS / 2 - checkerStackIndex + 1);
                } else if (checkerStackIndex >= 3 * NUM_POINTS / 4 + 1) {
                    return rightSide.getPoint(NUM_POINTS - checkerStackIndex + 1);
                } else {
                    return leftSide.getPoint(3 * NUM_POINTS / 4 - checkerStackIndex + 1);
                }
        }
        return null;
    }

    @Override
    public void update() {
        leftSide.update();
        rightSide.update();

        whiteHome.update();
        blackHome.update();

        whiteBar.update();
        blackBar.update();
    }

}