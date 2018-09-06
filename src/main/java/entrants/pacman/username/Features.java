package entrants.pacman.username;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

import java.util.ArrayList;

public class Features {

    private double food;
    private double pill;
    private double edibleGhost;
    private double ghostNear;
    private double runGhost;

    public Features(Game game, int node) {
        this.food = getFood(game, node);
        this.pill = getPill(game, node);
        this.edibleGhost = getEdible(game, node);
        this.ghostNear = ghostNear(game, node);
        this.runGhost = runGhost(game, node);
    }


    private double getFood(Game game, int node) {
        double food = game.getDistance(node,
                game.getClosestNodeIndexFromNodeIndex(node,
                        game.getActivePillsIndices(),
                        DM.PATH)
                , DM.PATH);
        return (1 / food);

    }

    private double getPill(Game game, int node) {
        if (game.getActivePowerPillsIndices().length > 0) {
            double food = game.getDistance(node,
                    game.getClosestNodeIndexFromNodeIndex(node,
                            game.getActivePowerPillsIndices(),
                            DM.PATH)
                    , DM.PATH);
            return (1 / food);
        } else {
            return 0;
        }
    }

    private double getEdible(Game game, int node) {
        ArrayList<GHOST> edibleGhosts = new ArrayList<>();
        for (GHOST ghost : GHOST.values()) {
            if (game.isGhostEdible(ghost)) {
                edibleGhosts.add(ghost);
            }
        }
        if (edibleGhosts.size() > 0) {
            double distance = Integer.MAX_VALUE;
            for (GHOST ghost : edibleGhosts) {
                double ghstDistance = game.getDistance(node, game.getGhostCurrentNodeIndex(ghost), DM.PATH);
                if (ghstDistance < distance && ghstDistance > 0) {
                    distance = game.getDistance(node, game.getGhostCurrentNodeIndex(ghost), DM.PATH);
                }
            }
            return 1 / distance;
        } else {
            return 0;
        }
    }

    public int ghostNear(Game game, int node) {
        if (edibleGhost > 0) {
            return 0;
        }
        for (GHOST ghost : GHOST.values()) {
            double dist = game.getDistance(node, game.getGhostCurrentNodeIndex(ghost), DM.PATH);
            if (dist > 0 && dist < 8) {
                return 1;
            }
        }
        return 0;
    }

    public double runGhost(Game game, int node) {
        double dist = Double.MAX_VALUE;
        for (GHOST ghost : GHOST.values()) {
            if (!game.isGhostEdible(ghost)) {
                double ghstDist = game.getDistance(node, game.getGhostCurrentNodeIndex(ghost), DM.PATH);
                if (ghstDist > 0 && ghstDist < dist && ghstDist < 50) {
                    dist = ghstDist;
                }
            }
        }
        if (dist > -1 && dist < 10) {
            setFood(0);
            setPwr(0);
            return 1 / dist;
        } else {
            return 0;
        }
    }

    public void setFood(int food) {
        this.food = food;
    }

    public void setPwr(int pwr) {
        this.pill = pwr;
    }

    public void setGhost(int ghost) {
        this.runGhost = ghost;
    }


    public double[] getFeatures() {
        return new double[]{
                this.food,
//                this.pill,
                this.edibleGhost,
//                this.ghostNear,
                this.runGhost
        };

    }


}
