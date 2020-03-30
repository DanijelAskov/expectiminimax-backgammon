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
 
package askov.schoolprojects.ai.expectiminimaxbackgammon.gameplay.player;

import askov.schoolprojects.ai.expectiminimaxbackgammon.gameplay.game.BoardNotSpecifiedException;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gameplay.player.move.Move;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Board;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Checker;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Die;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * @author  Danijel Askov
 */
public class ExpectiminimaxPlayer extends ComputerPlayer {

    private enum Node {
        MAX ("MAX"), MIN("MIN"), CHANCE("CHANCE");

        private String name;

        Node(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static final Node[] NODES = new Node[] {
        Node.MAX, Node.CHANCE, Node.MIN, Node.CHANCE
    };

    private static final Logger LOGGER = LogManager.getLogger(ExpectiminimaxPlayer.class.getName());
    private static final double HEURISTIC_COEFFICIENT = .055;
    private static final int DEFAULT_DEPTH = 3;

    private double[] moveQuality;
    private final Player opponent;
    private int initialDepth = -1;
    public StringBuilder treeStringRepresentationBuilder = new StringBuilder("");
    private boolean logExpectiminimaxTree = false;

    public ExpectiminimaxPlayer(Checker.CheckerColor checkerColor, Board board, boolean logExpectiminimaxTree) {
        super(checkerColor, board);
        this.logExpectiminimaxTree = logExpectiminimaxTree;
        opponent = new DummyPlayer(Checker.CheckerColor.getOppositeCheckerColor(checkerColor), board);
    }

    public ExpectiminimaxPlayer(Checker.CheckerColor checkerColor, Board board) {
        this(checkerColor, board, false);
    }

    public ExpectiminimaxPlayer(Checker.CheckerColor checkerColor, boolean logExpectiminimaxTree) {
        super(checkerColor);
        this.logExpectiminimaxTree = logExpectiminimaxTree;
        opponent = new DummyPlayer(Checker.CheckerColor.getOppositeCheckerColor(checkerColor), null);
    }

    public ExpectiminimaxPlayer(Checker.CheckerColor checkerColor) {
        this(checkerColor, false);
    }

    private double heuristicValue(int currentNodeIndex, int depth) {
        if (logExpectiminimaxTree)
            treeStringRepresentationBuilder.append("\t".repeat(initialDepth - depth + 1) + "No dice combinations analyzed, using heuristics\n");

        Checker.CheckerColor myColor = getCheckerColor();
        Checker.CheckerColor opponentColor = opponent.getCheckerColor();

        int numMyCheckersOnBar = board.getBar(myColor).getNumCheckers();
        int numMyCheckersInHome = board.getHome(myColor).getNumCheckers();

        int numOpponentCheckersOnBar = board.getBar(opponentColor).getNumCheckers();
        int numOpponentCheckersInHome = board.getHome(opponentColor).getNumCheckers();

        if (NODES[currentNodeIndex] == Node.MAX) {
            return HEURISTIC_COEFFICIENT * (numOpponentCheckersOnBar + numMyCheckersInHome - numMyCheckersOnBar - numOpponentCheckersInHome);
        } else { // Node.MIN
            return -HEURISTIC_COEFFICIENT * (numMyCheckersOnBar + numOpponentCheckersInHome - numOpponentCheckersOnBar - numMyCheckersInHome);
        }
    }

    private double expectiminimax(int depth, int currentNodeIndex) {
        double result = 0.;
        List<Move> moves;

        if (initialDepth == -1) initialDepth = depth;
        Die[] diceToPrint = NODES[currentNodeIndex] == Node.MAX ? getDice() : opponent.getDice();
        if (logExpectiminimaxTree)
            treeStringRepresentationBuilder.append("\t".repeat(initialDepth - depth) + NODES[currentNodeIndex] + (NODES[currentNodeIndex] != Node.CHANCE ? " [(" + diceToPrint[0] + ", " + diceToPrint[1] + ")] " : " ") + "{\n");

        switch (NODES[currentNodeIndex]) {
            case MAX:
                try {
                    generatePossibleMoves();
                } catch (BoardNotSpecifiedException ex) {
                    LOGGER.error(ex);
                }
                moves = this.getPossibleMoves();

                if (!moves.isEmpty()) {
                    double[] moveQuality = new double[moves.size()];

                    for (int i = 0; i < moves.size(); i++) {
                        if (logExpectiminimaxTree)
                            treeStringRepresentationBuilder.append("\t".repeat(initialDepth - depth) + moves.get(i) + " {\n");
                        make(moves.get(i));
                        moveQuality[i] = expectiminimax(depth - 1, (currentNodeIndex + 1) % 4);
                        unmake(moves.get(i));
                        if (logExpectiminimaxTree)
                            treeStringRepresentationBuilder.append("\t".repeat(initialDepth - depth) + "} [Move: " + moveQuality[i] + "]\n");
                    }

                    if (depth == initialDepth) {
                        this.moveQuality = new double[moveQuality.length];
                        System.arraycopy(moveQuality, 0, this.moveQuality, 0, moveQuality.length);
                    }
                    result = max(moveQuality);
                } else {
                    if (depth == initialDepth) moveQuality = new double[0];
                    result = -0.9;
                }
                break;
            case MIN:
                try {
                    opponent.generatePossibleMoves();
                } catch (BoardNotSpecifiedException ex) {
                    LOGGER.error(ex);
                }
                moves = opponent.getPossibleMoves();

                if (!moves.isEmpty()) {
                    double[] moveQuality = new double[moves.size()];

                    for (int i = 0; i < moves.size(); i++) {
                        if (logExpectiminimaxTree)
                            treeStringRepresentationBuilder.append("\t".repeat(initialDepth - depth) + moves.get(i) + " {\n");
                        opponent.make(moves.get(i));
                        moveQuality[i] = expectiminimax(depth - 1, (currentNodeIndex + 1) % 4);
                        opponent.unmake(moves.get(i));
                        if (logExpectiminimaxTree)
                            treeStringRepresentationBuilder.append("\t".repeat(initialDepth - depth) + "} [Move: " + moveQuality[i] + "]\n");
                    }

                    result = min(moveQuality);
                } else {
                    result = 0.9;
                }
                break;
            case CHANCE:
                if (depth == 0) { // No further tree traversal, use heuristics
                    result = heuristicValue(currentNodeIndex - 1, depth);
                } else {
                    Die[] dice = Die.DICE_PAIRS;
                    List<Double> values = new ArrayList<>();
                    for (int i = 0; i < dice.length; i += 2) {
                        Die[] currentDice;
                        if (NODES[(currentNodeIndex + 1) % 4] == Node.MAX) {
                            currentDice = getDice();
                            setDice(dice[i], dice[i + 1]);
                        } else {
                            currentDice = opponent.getDice();
                            opponent.setDice(dice[i], dice[i + 1]);
                        }
                        values.add(expectiminimax(depth - 1, (currentNodeIndex + 1) % 4));
                        if (NODES[(currentNodeIndex + 1) % 4] == Node.MAX) {
                            setDice(currentDice);
                        } else {
                            opponent.setDice(currentDice);
                        }
                    }
                    result = weightedAverage(values);
                }
        }
        if (logExpectiminimaxTree)
            treeStringRepresentationBuilder.append("\t".repeat(initialDepth - depth) + "} [" + NODES[currentNodeIndex] + ":" + result + "]\n");

        return result;
    }

    private static double min(double ...values) {
        double min = Double.MAX_VALUE;
        for (double value : values) {
            if (value < min)
                min = value;
        }
        return min;
    }

    private static double max(double ...values) {
        double max = -Double.MAX_VALUE;
        for (double value : values) {
            if (value > max)
                max = value;
        }
        return max;
    }

    private static double weightedAverage(List<Double> values) {
        double weightedSum = 0.;
        double coefficientSum = 0.;
        int i = 0;
        for (Double value : values) {
            double coefficient = Die.probability(Die.DICE_PAIRS[i++], Die.DICE_PAIRS[i++]);
            weightedSum += value * coefficient;
            coefficientSum += coefficient;
        }
        return weightedSum / coefficientSum;
    }

    @Override
    public Move getBestMove() {
        initialDepth = -1;
        Instant start = Instant.now();
        expectiminimax(DEFAULT_DEPTH, 0);
        Instant finish = Instant.now();
        LOGGER.info(String.format("Execution time for expectiminimax(%d,0): %d milliseconds", DEFAULT_DEPTH, Duration.between(start, finish).toMillis()));
        if (logExpectiminimaxTree)
            LOGGER.info("Generated Expectiminimax tree:\n" + treeStringRepresentationBuilder.toString());
        /*
            Depth should be an odd integer value (1, 3, 5, etc.)
            Depth grater than 3 causes a combinatorial explosion! Some kind of optimization is necessary (e.g. alpha-beta pruning)
         */
        initialDepth = -1;
        try {
            generatePossibleMoves();
        } catch (BoardNotSpecifiedException e) {
            e.printStackTrace();
        }

        int maxQualityMoveIndex = -1;
        double maxMoveQuality = -Double.MAX_VALUE;
        for (int i = 0; i < moveQuality.length; i++) {
            if (moveQuality[i] > maxMoveQuality) {
                maxMoveQuality = moveQuality[i];
                maxQualityMoveIndex = i;
            }
        }

        return maxQualityMoveIndex != -1 ? possibleMoves.get(maxQualityMoveIndex) : null;
    }

    @Override
    public void setBoard(Board board) {
        super.setBoard(board);
        opponent.setBoard(board);
    }

}