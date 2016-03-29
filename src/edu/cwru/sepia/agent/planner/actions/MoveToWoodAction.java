package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.TownHall;

/**
 * Created by james on 3/28/16.
 */
public class MoveToWoodAction implements StripsAction {

    final Peasant peasant;
    final TownHall townHall;

    public MoveToWoodAction(Peasant peasant, TownHall townHall) {
        this.peasant = peasant;
        this.townHall = townHall;
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        return !(peasant.isAdjacentGoldSource());
    }

    @Override
    public GameState apply(GameState state) {
        peasant.setAdjacentGoldSource(true);
        return null;
    }
}
