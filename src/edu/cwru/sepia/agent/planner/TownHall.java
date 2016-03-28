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

    public boolean canBuildPeasants(){
        //TODO modify this so that this will return true when the town hall can build a peasant.
        return allowedToBuildPeasants && true;
    }

    public int getCurrentGold() {
        return currentGold;
    }

    public int getCurrentWood() {
        return currentWood;
    }
}
