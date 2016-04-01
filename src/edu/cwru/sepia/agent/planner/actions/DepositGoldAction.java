package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.agent.planner.TownHall;

/**
 * Created by james on 3/28/16.
 */
public class DepositGoldAction implements StripsAction {

    public final int peasantID;
    public final int townHallID;
    private Position townHallPosition;

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
        GameState clone = new GameState(state);
        clone.peasants.get(peasantID).setHoldingGold(false);
        clone.townHall.depositGold(100);
        clone.incrementCost(1);
        clone.addAction(this);
        townHallPosition = clone.townHall.pos;
        return clone;
    }

    @Override
    public String toString() {
        return "Peasant with planning ID " + peasantID + " deposited gold at town hall with planning ID "
                + townHallID + " and position " + townHallPosition + ".\n";
    }
}
