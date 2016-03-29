package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.environment.model.state.ResourceNode;

/**
 * Created by james on 3/28/16.
 */
public class Resource {

    final int id;
    final Position position;
    int amountRemaining;
    final ResourceNode.Type type;

    public Resource(int id, Position position, int amountRemaining, ResourceNode.Type type) {
        this.id = id;
        this.position = position;
        this.amountRemaining = amountRemaining;
        this.type = type;
    }

    public Resource(Resource original){
        this.position = original.position;
        this.amountRemaining = original.amountRemaining;
        this.type = original.type;
        this.id = original.id;
    }

}
