package edu.cwru.sepia.agent.planner;

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

    public Peasant(int id) {
        this.id = id;
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

    public void setPosition(Position position) {
        this.position = position;
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
