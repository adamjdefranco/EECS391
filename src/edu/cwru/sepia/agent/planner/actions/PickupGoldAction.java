package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.*;
import edu.cwru.sepia.environment.model.state.ResourceNode;

/**
 * Created by james on 3/28/16.
 */
public class PickupGoldAction implements StripsAction {

    public final int peasantID;
    public final int resourceID;
    private Position goldPosition;

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
                && p.getPosition().isAdjacent(r.position)
                && r.getAmountRemaining() >= 100;
    }

    @Override
    public GameState apply(GameState state) {
        GameState clone = new GameState(state);
        Peasant p = clone.peasants.get(peasantID);
        Resource r = clone.resources.get(resourceID);
        r.takeResource(100);
        p.setHoldingGold(true);
        clone.incrementCost(1);
        clone.addAction(this);
        goldPosition = r.position;
        return clone;
    }

    @Override
    public String toString() {
        return "Peasant with planning ID " + peasantID + " picked up gold with planning ID "
                + resourceID + " and position " + goldPosition + ".";
    }

}
