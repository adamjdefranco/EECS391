package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.Resource;
import edu.cwru.sepia.agent.planner.TownHall;
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
        return state.peasants.containsKey(peasantID)
                && state.resources.containsKey(resourceID)
                && state.resources.get(resourceID).type == ResourceNode.Type.TREE
                && !(state.peasants.get(peasantID).isAdjacentGoldSource());
    }

    @Override
    public GameState apply(GameState state) {
        GameState clone = new GameState(state);
        clone.peasants.get(peasantID).setAdjacentWoodSource(true);
        clone.peasants.get(peasantID).setPosition(clone.resources.get(resourceID).position);
        double cost = state.peasants.get(peasantID).getPosition().euclideanDistance(state.resources.get(resourceID).position);
        clone.incrementCost(cost);
        clone.addAction(this);
        return clone;
    }

}
