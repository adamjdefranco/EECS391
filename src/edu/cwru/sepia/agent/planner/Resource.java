package edu.cwru.sepia.agent.planner;

import com.sun.javaws.exceptions.InvalidArgumentException;
import edu.cwru.sepia.environment.model.state.ResourceNode;

/**
 * Created by james on 3/28/16.
 */
public class Resource {

    public final int id;
    public final Position position;
    protected int amountRemaining;
    public final ResourceNode.Type type;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource resource = (Resource) o;

        if (id != resource.id) return false;
        if (amountRemaining != resource.amountRemaining) return false;
        if (!position.equals(resource.position)) return false;
        return type == resource.type;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + position.hashCode();
        result = 31 * result + amountRemaining;
        result = 31 * result + type.hashCode();
        return result;
    }

    public void takeResource(int amount){
        if(amount > amountRemaining){
            throw new IllegalArgumentException("amount");
        } else {
            amountRemaining -= amount;
        }
    }

    public int getAmountRemaining() {
        return amountRemaining;
    }
}
