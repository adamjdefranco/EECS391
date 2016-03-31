package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.TownHall;

/**
 * Created by james on 3/28/16.
 */
public class MoveToTownhallAction implements StripsAction {

    public final int peasantID;
    public final int townHallID;

    public MoveToTownhallAction(Peasant peasant, TownHall townHall) {
        this.peasantID = peasant.id;
        this.townHallID = townHall.id;
    }

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

    @Override
    public GameState apply(GameState state) {
        GameState clone = new GameState(state);
        clone.peasants.get(peasantID).setAdjacentTownHall(true);
        clone.peasants.get(peasantID).setPosition(clone.townHall.pos, clone);
        double cost = state.peasants.get(peasantID).getPosition().euclideanDistance(state.townHall.pos);
        clone.incrementCost(cost);
        clone.addAction(this);
        return clone;
    }

}
