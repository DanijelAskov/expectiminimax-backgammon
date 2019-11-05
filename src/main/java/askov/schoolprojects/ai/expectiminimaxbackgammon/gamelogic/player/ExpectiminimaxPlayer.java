/*
 * Copyright (c) 2017, Danijel Askov
 */

package askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player;

import askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.game.BoardNotSpecifiedException;
import askov.schoolprojects.ai.expectiminimaxbackgammon.gamelogic.player.move.Move;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Board;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Checker;
import askov.schoolprojects.ai.expectiminimaxbackgammon.sprites.Die;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author  Danijel Askov
 */
public class ExpectiminimaxPlayer extends ComputerPlayer {

    private enum Node {
        MAX, MIN, CHANCE
    }

    private static final Node[] NODES = new Node[] {
        Node.MAX, Node.CHANCE, Node.MIN, Node.CHANCE
    };

    private static final double HEURISTIC_COEFFICIENT = .055;

    private double[] moveQuality;
    private final Player opponent;
    private int initialDepth = -1;

    public ExpectiminimaxPlayer(Checker.CheckerColor checkerColor, Board board) {
        super(checkerColor, board);
        opponent = new DummyPlayer(Checker.CheckerColor.getOppositeCheckerColor(checkerColor), board);
    }

    public ExpectiminimaxPlayer(Checker.CheckerColor checkerColor) {
        super(checkerColor);
        opponent = new DummyPlayer(Checker.CheckerColor.getOppositeCheckerColor(checkerColor), null);
    }

    private double heuristicValue(int currentNodeIndex) {
        Checker.CheckerColor myColor = getCheckerColor();
        Checker.CheckerColor opponentColor = opponent.getCheckerColor();
        int numMyCheckersOnBar = board.getBar(myColor).getNumCheckers();
        int numOpponentCheckersOnBar = board.getBar(opponentColor).getNumCheckers();
        if (NODES[currentNodeIndex] == Node.MAX) {
            return HEURISTIC_COEFFICIENT * (numOpponentCheckersOnBar - numMyCheckersOnBar);
        } else { // Node.MIN
            return -HEURISTIC_COEFFICIENT * (numMyCheckersOnBar - numOpponentCheckersOnBar);
        }
    }

    private double expectiminimax(int depth, int currentNodeIndex) {
        double result = 0.;
        List<Move> moves;

        switch (NODES[currentNodeIndex]) {
            case MAX:
                if (initialDepth == -1) initialDepth = depth;

                try {
                    generatePossibleMoves();
                } catch (BoardNotSpecifiedException ex) {
                    Logger.getLogger(ExpectiminimaxPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
                moves = this.getPossibleMoves();

                if (!moves.isEmpty()) {
                    double[] moveQuality = new double[moves.size()];

                    for (int i = 0; i < moves.size(); i++) {
                        make(moves.get(i));
                        moveQuality[i] = expectiminimax(depth - 1, (currentNodeIndex + 1) % 4);
                        unmake(moves.get(i));
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
                    Logger.getLogger(ExpectiminimaxPlayer.class.getName()).log(Level.SEVERE, null, ex);
                }
                moves = opponent.getPossibleMoves();

                if (!moves.isEmpty()) {
                    double[] moveQuality = new double[moves.size()];

                    for (int i = 0; i < moves.size(); i++) {
                        opponent.make(moves.get(i));
                        moveQuality[i] = expectiminimax(depth - 1, (currentNodeIndex + 1) % 4);
                        opponent.unmake(moves.get(i));
                    }

                    result = min(moveQuality);
                } else {
                    result = 0.9;
                }
                break;
            case CHANCE:
                if (depth == 0) { // No further tree traversal, use heuristics
                    result = heuristicValue(currentNodeIndex - 1);
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
        expectiminimax(3, 0);
        /*
            Depth should be an odd integer value (1, 3, 5, etc.).
            Depth grater than 3 causes combinatorial explosion! Some kind of optimization is necessary (e.g. alpha-beta pruning).
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
