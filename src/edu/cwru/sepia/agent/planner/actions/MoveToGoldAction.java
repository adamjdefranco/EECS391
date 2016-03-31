package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.agent.planner.Resource;
import edu.cwru.sepia.environment.model.state.ResourceNode;

/**
 * Created by james on 3/28/16.
 */
public class MoveToGoldAction implements StripsAction {

    public final int peasantID;
    public final int resourceID;
    private Position goldPosition;

    public MoveToGoldAction(Peasant peasant, Resource goldResource) {
        this.peasantID = peasant.id;
        this.resourceID = goldResource.id;
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        if(!state.resources.containsKey(resourceID)){
            return false;
        }
        if(!state.peasants.containsKey(peasantID)){
            return false;
        }
        Resource r = state.resources.get(resourceID);
        Peasant p = state.peasants.get(peasantID);
        return r.type == ResourceNode.Type.GOLD_MINE
                && !(p.getPosition().isAdjacent(r.position));
    }

    @Override
    public GameState apply(GameState state) {
        GameState clone = new GameState(state);
        clone.peasants.get(peasantID).setPosition(clone.resources.get(resourceID).position, clone);
        clone.peasants.get(peasantID).setAdjacentGoldSource(true);
        clone.addAction(this);
        goldPosition = state.resources.get(resourceID).position;
        double cost = state.peasants.get(peasantID).getPosition().chebyshevDistance(goldPosition);
        clone.incrementCost(cost);
        return clone;
    }

    @Override
    public String toString() {
        return "Peasant with planning ID " + peasantID + " moved to gold at position " +
                goldPosition + " with planning ID " + resourceID + ".";
    }

}
