package edu.cwru.sepia.agent.planner;

/**
 * Created by james on 3/28/16.
 */
public class TownHall {

    // Variables for the town hall
    public final int requiredTotalGold;
    public final int requiredTotalWood;

    public final int id;
    public final Position pos;
    private final boolean allowedToBuildPeasants;

    private int currentGold = 0;
    private int currentWood = 0;

    private int populationCap;
    private int population;

    // Constructor
    public TownHall(int id, Position pos, int requiredTotalGold, int requiredTotalWood, int currentWood, int currentGold, boolean allowedToBuildPeasants, int populationCap, int population) {
        this.requiredTotalGold = requiredTotalGold;
        this.requiredTotalWood = requiredTotalWood;
        this.id = id;
        this.pos = pos;
        this.allowedToBuildPeasants = allowedToBuildPeasants;
        this.populationCap = populationCap;
        this.population = population;
        this.currentGold = currentGold;
        this.currentWood = currentWood;
    }

    // Constructor for a given town hall
    public TownHall(TownHall other) {
        this.requiredTotalGold = other.requiredTotalGold;
        this.requiredTotalWood = other.requiredTotalWood;
        this.id = other.id;
        this.pos = other.pos;
        this.allowedToBuildPeasants = other.allowedToBuildPeasants;
        this.currentGold = other.currentGold;
        this.currentWood = other.currentWood;
        this.population = other.population;
        this.populationCap = other.populationCap;
    }

    // Whether or not you can build peasants (part two of the assignment)
    public boolean canBuildPeasants(){
        return allowedToBuildPeasants && this.currentGold >= 400;
    }

    // Actions performed on town hall
    public void depositGold(int amountOfGold) { this.currentGold += amountOfGold; }

    public void depositWood(int amountOfWood) { this.currentWood += amountOfWood; }

    public int getCurrentGold() {
        return currentGold;
    }

    public int getCurrentWood() {
        return currentWood;
    }

    public int getPopulationCap() {
        return populationCap;
    }

    public int getPopulation() {
        return population;
    }

    // Creates a peasant and deducts 400 gold and adds one to the population
    public void makePeasant(){
        if(this.currentGold < 400){
            throw new IllegalArgumentException("gold");
        } else {
            this.currentGold -= 400;
            this.population += 1;
        }
    }

    // Equals checks that the required gold and wood is the same, the current gold and wood is the same, and the ability to build peasants is allowed
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
