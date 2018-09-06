
import entrants.pacman.username.RLPacman;
import examples.StarterGhostComm.Blinky;
import examples.StarterGhostComm.Inky;
import examples.StarterGhostComm.Pinky;
import examples.StarterGhostComm.Sue;
import examples.StarterPacManOneJunction.MyPacMan;
import pacman.Executor;
import pacman.controllers.IndividualGhostController;
import pacman.controllers.MASController;
import pacman.game.Constants.*;

import java.util.EnumMap;


/**
 * Created by pwillic on 06/05/2016.
 */
public class Main {

    public static void main(String[] args) {

        Executor executor = new Executor.Builder()
                .setPacmanPO(false)
                .setGhostPO(false)
                .setVisual(true)
                .setTickLimit(4000)
                .build();

        EnumMap<GHOST, IndividualGhostController> controllers = new EnumMap<>(GHOST.class);

        controllers.put(GHOST.INKY, new Inky());
        controllers.put(GHOST.BLINKY, new Blinky());
        controllers.put(GHOST.PINKY, new Pinky());
        controllers.put(GHOST.SUE, new Sue());

        RLPacman rlPacman = new RLPacman();

//        executor.runExperiment(rlPacman,new MASController(controllers),1000,"Test");

//        RLPacman.setEPSILON(0.0);
        RLPacman.printWeights();

        executor.runGameTimed(rlPacman, new MASController(controllers));

        RLPacman.printWeights();
    }
}
