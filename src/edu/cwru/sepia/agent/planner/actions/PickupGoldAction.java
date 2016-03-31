package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.Resource;
import edu.cwru.sepia.agent.planner.TownHall;
import edu.cwru.sepia.environment.model.state.ResourceNode;

/**
 * Created by james on 3/28/16.
 */
public class PickupGoldAction implements StripsAction {

    public final int peasantID;
    public final int resourceID;

    public PickupGoldAction(Peasant peasant, Resource resource) {
        this.peasantID = peasant.id;
        this.resourceID = resource.id;
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        if(!state.peasants.containsKey(peasantID)){
            return false;
        }
        if(!state.resources.containsKey(resourceID)){
            return false;
        }
        Peasant p = state.peasants.get(peasantID);
        Resource r = state.resources.get(resourceID);
        return r.type == ResourceNode.Type.GOLD_MINE
                && !(p.isHoldingGold())
                && !(p.isHoldingWood())
                && p.getPosition().isAdjacent(r.position);
    }

    @Override
    public GameState apply(GameState state) {
        GameState clone = new GameState(state);
        Peasant p = clone.peasants.get(peasantID);
        p.setHoldingGold(true);
        clone.incrementCost(1);
        clone.addAction(this);
        return clone;
    }

}
