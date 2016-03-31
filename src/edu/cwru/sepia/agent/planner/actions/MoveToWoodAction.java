package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.Resource;
import edu.cwru.sepia.environment.model.state.ResourceNode;

/**
 * Created by james on 3/28/16.
 */
public class MoveToWoodAction implements StripsAction {

    public final int peasantID;
    public final int resourceID;

    public MoveToWoodAction(Peasant peasant, Resource resource) {
        this.peasantID = peasant.id;
        this.resourceID = resource.id;
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
        return r.type == ResourceNode.Type.TREE
                && !(p.getPosition().isAdjacent(r.position));
    }

    @Override
    public GameState apply(GameState state) {
        GameState clone = new GameState(state);
        clone.peasants.get(peasantID).setPosition(clone.resources.get(resourceID).position, clone);
        clone.peasants.get(peasantID).setAdjacentWoodSource(true);
        double cost = state.peasants.get(peasantID).getPosition().euclideanDistance(state.resources.get(resourceID).position);
        clone.incrementCost(cost);
        clone.addAction(this);
        return clone;
    }

}
