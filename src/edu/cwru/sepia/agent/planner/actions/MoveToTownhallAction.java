package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.TownHall;

/**
 * Created by james on 3/28/16.
 */
public class MoveToTownhallAction implements StripsAction {

    final Peasant peasant;
    final TownHall townHall;

    public MoveToTownhallAction(Peasant peasant, TownHall townHall) {
        this.peasant = peasant;
        this.townHall = townHall;
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        return !(peasant.isAdjacentTownHall());
    }

    @Override
    public GameState apply(GameState state) {
        peasant.setAdjacentTownHall(true);
        return null;
    }
}
