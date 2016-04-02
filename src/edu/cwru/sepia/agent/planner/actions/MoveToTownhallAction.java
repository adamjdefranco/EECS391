package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.agent.planner.TownHall;

// Class to check preconditions for a peasant to move to town hall and applying it to a state
public class MoveToTownhallAction implements StripsAction {

    public final int peasantID;
    public final int townHallID;
    private Position townHallPosition;

    // Constructor
    public MoveToTownhallAction(Peasant peasant, TownHall townHall) {
        this.peasantID = peasant.id;
        this.townHallID = townHall.id;
    }

    // Preconditions are only met if the peasant is not already adjacent to town hall
    @Override
    public boolean preconditionsMet(GameState state) {
        if(!state.peasants.containsKey(peasantID)){
            return false;
        }
        if(state.townHall.id != townHallID){
            return false;
        }
        return !(state.peasants.get(peasantID).getPosition().isAdjacent(state.townHall.pos));
    }

    // Applies the action (moving the peasant to the town hall) to the given state
    @Override
    public GameState apply(GameState state) {
        GameState clone = new GameState(state);
        clone.peasants.get(peasantID).setAdjacentTownHall(true);
        clone.peasants.get(peasantID).setPosition(clone.townHall.pos, clone);
        townHallPosition = state.townHall.pos;
        double cost = state.peasants.get(peasantID).getPosition().chebyshevDistance(townHallPosition);
        clone.incrementCost(cost);
        clone.addAction(this);
        return clone;
    }

    @Override
    public String toString() {
        return "Peasant with planning ID " + peasantID + " moved to town hall with planning ID "
                + townHallID + " and position " + townHallPosition + ".";
    }

}
