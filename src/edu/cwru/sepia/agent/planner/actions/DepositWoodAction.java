package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.agent.planner.TownHall;

// Class to check preconditions for a peasant to deposit wood at town hall and applying it to a state
public class DepositWoodAction implements StripsAction {

    public final int peasantID;
    public final int townHallID;
    private Position townHallPosition;

    // Constructor
    public DepositWoodAction(Peasant peasant, TownHall townHall) {
        this.peasantID = peasant.id;
        this.townHallID = townHall.id;
    }


    // Preconditions are only true if the peasant is holding wood and is adjacent to town hall
    @Override
    public boolean preconditionsMet(GameState state) {
        return state.peasants.containsKey(peasantID)
                && state.townHall.id == townHallID
                && state.peasants.get(peasantID).isHoldingWood()
                && state.peasants.get(peasantID).isAdjacentTownHall();
    }

    // Applies the state (a peasant depositing wood into town hall) to the given state
    @Override
    public GameState apply(GameState state) {
        GameState clone = new GameState(state);
        clone.peasants.get(peasantID).setHoldingWood(false);
        clone.townHall.depositWood(100);
        clone.incrementCost(1);
        clone.addAction(this);
        townHallPosition = clone.townHall.pos;
        return clone;
    }

    @Override
    public String toString() {
        return "Peasant with planning ID " + peasantID + " deposited wood at town hall with planning ID "
                + townHallID + " and position " + townHallPosition + ".\n";
    }

}
