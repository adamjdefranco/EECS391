package edu.cwru.sepia.agent.planner;

/**
 * Created by james on 3/28/16.
 */
public class TownHall {

    public final int requiredTotalGold;
    public final int requiredTotalWood;

    public final int id;
    public final Position pos;
    private final boolean allowedToBuildPeasants;

    private int currentGold = 0;
    private int currentWood = 0;

    public TownHall(int id, Position pos, int requiredTotalGold, int requiredTotalWood, boolean allowedToBuildPeasants) {
        this.requiredTotalGold = requiredTotalGold;
        this.requiredTotalWood = requiredTotalWood;
        this.id = id;
        this.pos = pos;
        this.allowedToBuildPeasants = allowedToBuildPeasants;
    }

    public TownHall(TownHall other) {
        this.requiredTotalGold = other.requiredTotalGold;
        this.requiredTotalWood = other.requiredTotalWood;
        this.id = other.id;
        this.pos = other.pos;
        this.allowedToBuildPeasants = other.allowedToBuildPeasants;
        this.currentGold = other.currentGold;
        this.currentWood = other.currentWood;
    }

    public boolean canBuildPeasants(){
        //TODO modify this so that this will return true when the town hall can build a peasant.
        return allowedToBuildPeasants && this.currentGold >= 400;
    }

    public void depositGold(int amountOfGold) { this.currentGold += amountOfGold; }

    public void depositWood(int amountOfWood) { this.currentWood += amountOfWood; }

    public int getCurrentGold() {
        return currentGold;
    }

    public int getCurrentWood() {
        return currentWood;
    }

    public void makePeasant(){
        if(this.currentGold < 400){
            throw new IllegalArgumentException("gold");
        } else {
            this.currentGold -= 400;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TownHall townHall = (TownHall) o;

        if (requiredTotalGold != townHall.requiredTotalGold) return false;
        if (requiredTotalWood != townHall.requiredTotalWood) return false;
        if (allowedToBuildPeasants != townHall.allowedToBuildPeasants) return false;
        if (getCurrentGold() != townHall.getCurrentGold()) return false;
        return getCurrentWood() == townHall.getCurrentWood();

    }

    @Override
    public int hashCode() {
        int result = requiredTotalGold;
        result = 31 * result + requiredTotalWood;
        result = 31 * result + (allowedToBuildPeasants ? 1 : 0);
        result = 31 * result + getCurrentGold();
        result = 31 * result + getCurrentWood();
        return result;
    }
}
