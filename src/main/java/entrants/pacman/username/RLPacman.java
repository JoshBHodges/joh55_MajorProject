package entrants.pacman.username;

import pacman.controllers.PacmanController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.*;

/*
 * This is the class you need to modify for your entry. In particular, you need to
 * fill in the getMove() method. Any additional classes you write should either
 * be placed in this package or sub-packages (e.g., entrants.pacman.username).
 */
public class RLPacman extends PacmanController {
    private static double ALPHA = 1, GAMMA = 1, EPSILON = 0.0;
    private MOVE myMove = MOVE.NEUTRAL;
    private static double[] weights;
    private Features features;

    public RLPacman() {
        weights = new double[]{0,0,0};
    }

    public MOVE getMove(Game game, long timeDue) {
        //Place your game logic here to play the game as Ms Pac-Man

        int node = game.getPacmanCurrentNodeIndex();
        double[] features = new Features(game.copy(), node).getFeatures();


        double[] Q = QTable(game.copy(), node);

        if (new Random().nextDouble() < EPSILON) {
            MOVE[] moves = game.getPossibleMoves(node, game.getPacmanLastMoveMade());
            if (moves.length > 0) {
                myMove = moves[new Random().nextInt(moves.length)];
            }
        } else {
            myMove = game.getPossibleMoves(node)[maxQIndex(Q)];
        }

        updateWeights(game.copy(), myMove, maxQ(Q), features);

//        System.out.println(Arrays.toString(Q) + "," + Arrays.toString(weights) + "," + Arrays.toString(features) + "," + Arrays.toString(game.getPossibleMoves(node)) + ">" + myMove);

        return myMove;
    }

    private double[] QTable(Game game, int node) {
        double[] Q = new double[game.getPossibleMoves(node).length];
        MOVE[] moves = game.getPossibleMoves(node);
        for (int i = 0; i < moves.length; i++) {
            Q[i] = getQ(game.copy(), moves[i]);
        }
        return Q;
    }

    private double getQ(Game game, MOVE move) {
        int[] pills = game.getActivePillsIndices();
        int[] power = game.getActivePowerPillsIndices();
        double Q = 0;
        game.updatePacMan(move);
        int node = game.getPacmanCurrentNodeIndex();
        features = new Features(game.copy(), node);
        for (int n : pills) {
            if (node == n) {
                features.setFood(1);
            }
        }
        for (int n : power) {
            if (node == n) {
                features.setPwr(1);
            }
        }
        for (int j = 0; j < weights.length; j++) {
            Q += features.getFeatures()[j] * weights[j];
        }
        return Q;
    }

    private int maxQIndex(double[] QTable) {
        ArrayList<Double> Q = new ArrayList<>();
        for (double q : QTable) {
            Q.add(q);
        }
        return Q.indexOf(Collections.max(Q));
    }

    private double maxQ(double[] QTable) {
        ArrayList<Double> Q = new ArrayList<>();
        for (double q : QTable) {
            Q.add(q);
        }
        return Collections.max(Q);
    }

    private void updateWeights(Game game, MOVE myMove, double Q, double[] features) {
        game.updatePacMan(myMove);
        int node = game.getPacmanCurrentNodeIndex();
        double delta = (reward(game) + GAMMA * maxQ(QTable(game, node)) - Q);
        for (int i = 0; i < weights.length; i++) {
            weights[i] = weights[i] + (ALPHA * delta) * features[i];
        }
    }

    private int reward(Game game) {
        int reward = 0;
        if (game.wasPillEaten()) {
            reward += 5;
        }
        if (game.wasPowerPillEaten()) {
            reward += 25;
        }
        if (game.wasPacManEaten()) {
            reward += -30;
            features.setPwr(0);
            features.setFood(0);
            features.setGhost(1);
        }
        for (GHOST ghost : GHOST.values()) {
            if (game.wasGhostEaten(ghost)) {
                reward += 30;
            }
        }
//        System.out.print(reward);
        return reward;
    }

    public static void setEPSILON(double EPSILON) {
        RLPacman.EPSILON = EPSILON;
    }

    public static void printWeights() {
        System.out.println(Arrays.toString(weights));
    }
}