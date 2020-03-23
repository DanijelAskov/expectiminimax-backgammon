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

package askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.animation;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

/**
 * @author  Danijel Askov
 */
public class PulsingBorderAnimation extends ShapeAnimation {

    private final Timeline timeline;
    private final double originalStrokeWidth;
    private final Paint originalStroke;

    public PulsingBorderAnimation(Shape shape) {
        super(shape);

        originalStrokeWidth = shape.getStrokeWidth();
        originalStroke = shape.getStroke();

        timeline = new Timeline();

        KeyValue startStrokeWidthKeyValue = new KeyValue(shape.strokeWidthProperty(), 0);
        KeyValue startStrokeColorKeyValue = new KeyValue(shape.strokeProperty(), Color.BLACK);
        KeyFrame startKeyFrame = new KeyFrame(Duration.seconds(0), startStrokeWidthKeyValue, startStrokeColorKeyValue);

        KeyValue endStrokeWidthKeyValue = new KeyValue(shape.strokeWidthProperty(), 4);
        KeyValue endStrokeColorKeyValue = new KeyValue(shape.strokeProperty(), Color.YELLOW);
        KeyFrame endKeyFrame = new KeyFrame(Duration.seconds(0.40), endStrokeColorKeyValue, endStrokeWidthKeyValue);

        timeline.getKeyFrames().addAll(startKeyFrame, endKeyFrame);

        timeline.setAutoReverse(true);
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
    }

    @Override
    public void start() {
        if (timeline.getStatus() == javafx.animation.Animation.Status.RUNNING) timeline.stop();
        timeline.play();
    }

    @Override
    public void stop() {
        timeline.stop();
        shape.setStrokeWidth(originalStrokeWidth);
        shape.setStroke(originalStroke);
    }

}