package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.TownHall;

/**
 * Created by james on 3/28/16.
 */
public class DepositWoodAction implements StripsAction {

    final int peasantID;
    final int townHallID;

    public DepositWoodAction(Peasant peasant, TownHall townHall) {
        this.peasantID = peasant.id;
        this.townHallID = townHall.id;
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        return state.peasants.containsKey(peasantID)
                && state.townHall.id == townHallID
                && state.peasants.get(peasantID).isHoldingWood()
                && state.peasants.get(peasantID).isAdjacentTownHall();
    }

    @Override
    public GameState apply(GameState state) {
        state.peasants.get(peasantID).setHoldingWood(false);
        state.townHall.incrementWood(100);
        return state;
    }

    @Override
    public double getCost(GameState state) {
        return 1;
    }
}
