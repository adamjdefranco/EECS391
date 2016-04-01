package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.agent.planner.TownHall;

/**
 * Created by james on 3/31/16.
 */
public class BuildPeasantAction implements StripsAction {

    final int townhallID;
    private int newPeasantID;
    private Position townHallPosition;

    public BuildPeasantAction(TownHall townhall) {
        this.townhallID = townhall.id;
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        return state.townHall.id == townhallID
                && state.townHall.canBuildPeasants()
                && state.townHall.getPopulationCap() > state.townHall.getPopulation();
    }

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
