package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.agent.planner.TownHall;

// Class to check preconditions for a peasant to deposit gold at town hall and applying it to a state
public class DepositGoldAction implements StripsAction {

    public final int peasantID;
    public final int townHallID;
    private Position townHallPosition;

    // Constructor
    public DepositGoldAction(Peasant peasant, TownHall townHall) {
        this.peasantID = peasant.id;
        this.townHallID = townHall.id;
    }

    // The preconditions are met if the peasant is holding gold and is adjacent to town hall.
    @Override
    public boolean preconditionsMet(GameState state) {
        return state.peasants.containsKey(peasantID)
                && state.townHall.id == townHallID
                && state.peasants.get(peasantID).isHoldingGold()
                && state.peasants.get(peasantID).isAdjacentTownHall();
    }

    // Applies the action (depositing gold at town hall) to the given state
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
