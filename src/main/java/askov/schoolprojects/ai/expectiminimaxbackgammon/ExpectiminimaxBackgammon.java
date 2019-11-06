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

package askov.schoolprojects.ai.expectiminimaxbackgammon;

import askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.game.Game;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.game.HumanVsComputerGame;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.game.HumanVsHumanGame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author  Danijel Askov
 */
public class ExpectiminimaxBackgammon extends Application {

    private static final String HUMAN_VS_COMPUTER = "Human vs Computer";
    private static final String HUMAN_VS_HUMAN = "Human vs Human";

    private static final double WIDTH = 850;
    private static final double HEIGHT = 600;
    
    @Override
    public void start(Stage primaryStage) {
        List<String> choices = new ArrayList<>();
        choices.add(HUMAN_VS_COMPUTER);
        choices.add(HUMAN_VS_HUMAN);

        ChoiceDialog<String> dialog = new ChoiceDialog<>(HUMAN_VS_COMPUTER, choices);
        dialog.setTitle("ExpectiminimaxBackgammon");
        dialog.setHeaderText("Game Mode");
        dialog.setContentText("Specify who do you want to play against by choosing one of the two game modes:");

        Game game = null;

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            if (HUMAN_VS_HUMAN.equals(result.get())) {
                game = new HumanVsHumanGame(WIDTH, HEIGHT);
            } else {
                game = new HumanVsComputerGame(WIDTH, HEIGHT);
            }
        } else {
            Platform.exit();
        }

        if (game != null) {
            Scene scene = new Scene(game.getBoard(), WIDTH, HEIGHT);

            primaryStage.setTitle("Expectiminimax Backgammon | " + result.get());
            primaryStage.setScene(scene);
            game.setStage(primaryStage);
            primaryStage.setResizable(false);
            primaryStage.sizeToScene();
            primaryStage.show();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}