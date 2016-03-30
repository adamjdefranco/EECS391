package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.TownHall;

/**
 * Created by james on 3/28/16.
 */
public class DepositGoldAction implements StripsAction {

    final int peasantID;
    final int townHallID;

    public DepositGoldAction(Peasant peasant, TownHall townHall) {
        this.peasantID = peasant.id;
        this.townHallID = townHall.id;
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        return state.peasants.containsKey(peasantID)
                && state.townHall.id == townHallID
                && state.peasants.get(peasantID).isHoldingGold()
                && state.peasants.get(peasantID).isAdjacentTownHall();
    }

    @Override
    public GameState apply(GameState state) {
        state.peasants.get(peasantID).setHoldingGold(false);
        state.townHall.incrementGold(100);
        // TODO: How do we update the gamestate to reflect this?
        return state;
    }

    @Override
    public double getCost(GameState state) {
        return 1;
    }
}
