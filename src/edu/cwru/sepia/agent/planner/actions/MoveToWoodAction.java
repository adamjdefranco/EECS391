package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.Position;
import edu.cwru.sepia.agent.planner.Resource;
import edu.cwru.sepia.environment.model.state.ResourceNode;

// Class to check preconditions for a peasant moving to wood and applying it to a state
public class MoveToWoodAction implements StripsAction {

    public final int peasantID;
    public final int resourceID;
    private Position woodLocation;

    // Constructor
    public MoveToWoodAction(Peasant peasant, Resource resource) {
        this.peasantID = peasant.id;
        this.resourceID = resource.id;
    }

    // Preconditions are only true if the peasant is not already adjacent to wood and there is a tree to move to
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

    // Applies the action (The peasant moving to gold) to the given state
    @Override
    public GameState apply(GameState state) {
        GameState clone = new GameState(state);
        clone.peasants.get(peasantID).setPosition(clone.resources.get(resourceID).position, clone);
        clone.peasants.get(peasantID).setAdjacentWoodSource(true);
        woodLocation = state.resources.get(resourceID).position;
        double cost = state.peasants.get(peasantID).getPosition().chebyshevDistance(woodLocation);
        clone.incrementCost(cost);
        clone.addAction(this);
        return clone;
    }

    @Override
    public String toString() {
        return "Peasant with planning ID " + peasantID + " moved to wood with planning ID "
                + resourceID + " and location " + woodLocation + ".";
    }

}
