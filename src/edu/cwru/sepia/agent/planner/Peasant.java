package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.environment.model.state.ResourceNode;

/**
 * Created by james on 3/28/16.
 */
public class Peasant {

    public final int id;
    private boolean holdingGold = false;
    private boolean holdingWood = false;
    private boolean adjacentTownHall = false;
    private boolean adjacentGoldSource = false;
    private boolean adjacentWoodSource = false;
    private Position position;

    public Peasant(int id, Position position) {
        this.id = id;
        this.position = position;
    }

    public Peasant(Peasant other) {
        this.id = other.id;
        this.holdingGold = other.holdingGold;
        this.holdingWood = other.holdingWood;
        this.adjacentTownHall = other.adjacentTownHall;
        this.adjacentGoldSource = other.adjacentGoldSource;
        this.adjacentWoodSource = other.adjacentWoodSource;
        this.position = other.position;
    }

    public boolean isHoldingGold() {
        return holdingGold;
    }

    public void setHoldingGold(boolean holdingGold) {
        this.holdingGold = holdingGold;
    }

    public boolean isHoldingWood() {
        return holdingWood;
    }

    public void setHoldingWood(boolean holdingWood) {
        this.holdingWood = holdingWood;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position, GameState state) {
        this.position = new Position(position);
        updatePeasantLocationVariables(state);
    }

    public boolean isAdjacentTownHall() {
        return adjacentTownHall;
    }

    public void setAdjacentTownHall(boolean adjacentTownHall) {
        this.adjacentTownHall = adjacentTownHall;
    }

    public boolean isAdjacentGoldSource() {
        return adjacentGoldSource;
    }

    public void setAdjacentGoldSource(boolean adjacentGoldSource) {
        this.adjacentGoldSource = adjacentGoldSource;
    }

    public boolean isAdjacentWoodSource() {
        return adjacentWoodSource;
    }

    public void setAdjacentWoodSource(boolean adjacentWoodSource) {
        this.adjacentWoodSource = adjacentWoodSource;
    }

    private void updatePeasantLocationVariables(GameState state){
        setAdjacentTownHall(false);
        setAdjacentGoldSource(false);
        setAdjacentWoodSource(false);
        if(state.townHall != null && getPosition().isAdjacent(state.townHall.pos)){
            setAdjacentTownHall(true);
        }
        for(Resource resource : state.resources.values()){
            if(resource.type == ResourceNode.Type.GOLD_MINE && getPosition().isAdjacent(resource.position)){
                setAdjacentGoldSource(true);
            }
            if(resource.type == ResourceNode.Type.TREE && getPosition().isAdjacent(resource.position)){
                setAdjacentWoodSource(true);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Peasant peasant = (Peasant) o;

        if (id != peasant.id) return false;
        if (isHoldingGold() != peasant.isHoldingGold()) return false;
        if (isHoldingWood() != peasant.isHoldingWood()) return false;
        if (isAdjacentTownHall() != peasant.isAdjacentTownHall()) return false;
        if (isAdjacentGoldSource() != peasant.isAdjacentGoldSource()) return false;
        if (isAdjacentWoodSource() != peasant.isAdjacentWoodSource()) return false;
        return getPosition() != null ? getPosition().equals(peasant.getPosition()) : peasant.getPosition() == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (isHoldingGold() ? 1 : 0);
        result = 31 * result + (isHoldingWood() ? 1 : 0);
        result = 31 * result + (isAdjacentTownHall() ? 1 : 0);
        result = 31 * result + (isAdjacentGoldSource() ? 1 : 0);
        result = 31 * result + (isAdjacentWoodSource() ? 1 : 0);
        result = 31 * result + (getPosition() != null ? getPosition().hashCode() : 0);
        return result;
    }
}
