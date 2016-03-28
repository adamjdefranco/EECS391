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
}
