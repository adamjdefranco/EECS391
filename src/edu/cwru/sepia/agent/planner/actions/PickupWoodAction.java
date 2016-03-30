package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.Resource;
import edu.cwru.sepia.agent.planner.TownHall;
import edu.cwru.sepia.environment.model.state.ResourceNode;

/**
 * Created by james on 3/28/16.
 */
public class PickupWoodAction implements StripsAction {

    final int peasantID;
    final int resourceID;

    public PickupWoodAction(Peasant peasant, Resource resource) {
        this.peasantID = peasant.id;
        this.resourceID = resource.id;
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        Peasant p = state.peasants.get(peasantID);
        return state.peasants.containsKey(peasantID)
                && state.resources.containsKey(resourceID)
                && state.resources.get(resourceID).type == ResourceNode.Type.TREE
                && !(p.isHoldingGold())
                && p.isAdjacentGoldSource();
    }

    @Override
    public GameState apply(GameState state) {
        Peasant p = state.peasants.get(peasantID);
        p.setHoldingWood(true);
        return state;
    }

    @Override
    public double getCost(GameState state) {
        return 1;
    }
}
