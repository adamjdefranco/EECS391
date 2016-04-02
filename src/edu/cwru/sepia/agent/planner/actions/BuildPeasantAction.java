package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.agent.planner.TownHall;

// Class to check preconditions for building a peasant and applying it to a state
public class BuildPeasantAction implements StripsAction {

    public final int townhallID;
    private int newPeasantID;
    private Position townHallPosition;

    // Constructor
    public BuildPeasantAction(TownHall townhall) {
        this.townhallID = townhall.id;
    }

    // Checks if the townhall can build a peasant (has enough gold and food) and if the population cap is not met. Precondition true if so
    @Override
    public boolean preconditionsMet(GameState state) {
        return state.townHall.id == townhallID
                && state.townHall.canBuildPeasants()
                && state.townHall.getPopulationCap() > state.townHall.getPopulation();
    }

    // Applies the new peasant to the given state
    @Override
    public GameState apply(GameState state) {
        GameState clone = new GameState(state);
        clone.townHall.makePeasant();
        clone.addAction(this);
        clone.incrementCost(1);
        newPeasantID = clone.peasants.size()+1;
        clone.peasants.put(newPeasantID,new Peasant(newPeasantID, state.townHall.pos));
        townHallPosition = clone.townHall.pos;
        return clone;
    }

    @Override
    public String toString() {
        return "Town hall with planning ID " + townhallID + " at position " + townHallPosition +
                " spawned a new peasant with planning ID " + newPeasantID + ".";
    }
}
