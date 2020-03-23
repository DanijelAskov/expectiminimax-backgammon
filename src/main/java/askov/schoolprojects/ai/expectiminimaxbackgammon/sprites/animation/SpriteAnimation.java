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

import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Sprite;

/**
 * @author  Danijel Askov
 */
public abstract class SpriteAnimation extends Animation {

    protected final Sprite sprite;

    public SpriteAnimation(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public abstract void start();

    @Override
    public abstract void stop();

}