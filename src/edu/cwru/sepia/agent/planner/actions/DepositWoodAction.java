package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.TownHall;

/**
 * Created by james on 3/28/16.
 */
public class DepositWoodAction implements StripsAction {

    public final int peasantID;
    public final int townHallID;

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
        GameState clone = new GameState(state);
        clone.peasants.get(peasantID).setHoldingWood(false);
        clone.townHall.depositWood(100);
        clone.incrementCost(1);
        clone.addAction(this);
        return clone;
    }

}
