package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.agent.planner.actions.*;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Unit;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is used to represent the state of the game after applying one of the available actions. It will also
 * track the A* specific information such as the parent pointer and the costToGetHere and heuristic function. Remember that
 * unlike the path planning A* from the first assignment the costToGetHere of an action may be more than 1. Specifically the costToGetHere
 * of executing a compound action such as move can be more than 1. You will need to account for this in your heuristic
 * and your costToGetHere function.
 * <p>
 * The first instance is constructed from the StateView object (like in PA2). Implement the methods provided and
 * add any other methods and member variables you need.
 * <p>
 * Some useful API calls for the state view are
 * <p>
 * state.getXExtent() and state.getYExtent() to get the map size
 * <p>
 * I recommend storing the actions that generated the instance of the GameState in this class using whatever
 * class/structure you use to represent actions.
 */
public class GameState implements Comparable<GameState> {

    public TownHall townHall;
    public Map<Integer, Peasant> peasants;
    public Map<Integer, Resource> resources;
    public List<StripsAction> actions;
    private double costToGetHere = 0.0;
    private int populationCap;
    private int population;
    /**
     * Construct a GameState from a stateview object. This is used to construct the initial search node. All other
     * nodes should be constructed from the another constructor you create or by factory functions that you create.
     *
     * @param state         The current stateview at the time the plan is being created
     * @param playernum     The player number of agent that is planning
     * @param requiredGold  The goal amount of gold (e.g. 200 for the small scenario)
     * @param requiredWood  The goal amount of wood (e.g. 200 for the small scenario)
     * @param buildPeasants True if the BuildPeasant action should be considered
     */
    public GameState(State.StateView state, int playernum, int requiredGold, int requiredWood, boolean buildPeasants) {
        peasants = new HashMap<>();
        actions = new ArrayList<>();
        populationCap = state.getSupplyCap(playernum);
        population = state.getSupplyAmount(playernum);
        resources = state.getAllResourceIds().stream().map(state::getResourceNode).collect(Collectors.toMap(ResourceNode.ResourceView::getID, c -> new Resource(c.getID(), Position.forResource(c), c.getAmountRemaining(), c.getType())));
        for (Unit.UnitView unit : state.getUnitIds(playernum).stream().map(state::getUnit).collect(Collectors.toList())) {
            String unitType = unit.getTemplateView().getName().toLowerCase();
            if (unitType.equals("townhall")) {
                this.townHall = new TownHall(unit.getID(),
                        Position.forUnit(unit),
                        requiredGold,
                        requiredWood,
                        buildPeasants);
                this.peasants.values().stream().filter(p -> p.getPosition().isAdjacent(townHall.pos)).forEach(peasant -> peasant.setAdjacentTownHall(true));
            } else if (unitType.equals("peasant")) {
                Peasant p = new Peasant(peasants.size() + 1, Position.forUnit(unit));
                peasants.put(p.id, p);
                if (this.townHall != null && p.getPosition().isAdjacent(townHall.pos)) {
                    p.setAdjacentTownHall(true);
                }
                for (Resource resource : resources.values()) {
                    if (p.isAdjacentGoldSource() && p.isAdjacentTownHall() && p.isAdjacentWoodSource()) {
                        //No point continuing to search to see if the unit is adjacent to things... this probably wont be triggered.
                        break;
                    }
                    if (resource.type == ResourceNode.Type.GOLD_MINE && !p.isAdjacentGoldSource() && p.getPosition().isAdjacent(resource.position)) {
                        p.setAdjacentGoldSource(true);
                    }
                    if (resource.type == ResourceNode.Type.TREE && !p.isAdjacentWoodSource() && p.getPosition().isAdjacent(resource.position)) {
                        p.setAdjacentWoodSource(true);
                    }
                }
                if (unit.getCargoAmount() > 0) {
                    p.setHoldingGold(unit.getCargoType() == ResourceType.GOLD);
                    p.setHoldingWood(unit.getCargoType() == ResourceType.WOOD);
                }
            }
        }
    }

    public GameState(GameState old) {
        this(old.resources, old.peasants, old.townHall, old.actions, old.costToGetHere, old.population, old.populationCap);
    }

    public GameState(Map<Integer, Resource> resources, Map<Integer, Peasant> peasants, TownHall townHall, List<StripsAction> actions, double previousCost, int population, int populationCap) {
        this.resources = resources.values().stream().map(Resource::new).collect(Collectors.toMap(r -> r.id, r -> r));
        this.peasants = peasants.values().stream().map(Peasant::new).collect(Collectors.toMap(r -> r.id, r -> r));
        this.townHall = new TownHall(townHall);
        this.actions = new ArrayList<>(actions);
        this.costToGetHere = previousCost;
    }

    /**
     * Unlike in the first A* assignment there are many possible goal states. As long as the wood and gold requirements
     * are met the peasants can be at any location and the capacities of the resource locations can be anything. Use
     * this function to check if the goal conditions are met and return true if they are.
     *
     * @return true if the goal conditions are met in this instance of game state.
     */
    public boolean isGoal() {
        return townHall.getCurrentGold() >= townHall.requiredTotalGold && townHall.getCurrentWood() >= townHall.requiredTotalWood;
    }

    public int getPopulationCap() {
        return populationCap;
    }

    public int getPopulation() {
        return population;
    }

    /**
     * The branching factor of this search graph are much higher than the planning. Generate all of the possible
     * successor states and their associated actions in this method.
     *
     * @return A list of the possible successor states and their associated actions
     */
    public List<GameState> generateChildren() {
        List<GameState> gameStates = new ArrayList<>();
        gameStates.add(this);
        return generateChildrenHelper(gameStates, this.peasants.values().iterator());
    }

    private List<GameState> generateChildrenHelper(List<GameState> createdStates, Iterator<Peasant> peasants) {
        if (peasants.hasNext()) {
            Peasant p = peasants.next();
            List<GameState> newlyCreatedStates = new ArrayList<>();
            for (GameState newState : createdStates) {
                List<StripsAction> peasantActions = newState.generateActionsForPeasant(p);
                for (StripsAction a : peasantActions) {
                    GameState postApplication = a.apply(newState);
                    newlyCreatedStates.add(postApplication);
                }
            }
            if (peasants.hasNext()) {
                return generateChildrenHelper(newlyCreatedStates, peasants);
            } else {
                return newlyCreatedStates;
            }
        } else {
            return createdStates;
        }
    }

    private List<StripsAction> generateActionsForPeasant(Peasant p) {
        List<StripsAction> actions = new ArrayList<>();
        for (Resource resource : resources.values()) {
            switch (resource.type) {
                case GOLD_MINE:
                    MoveToGoldAction goldMoveAction = new MoveToGoldAction(p, resource);
                    if (goldMoveAction.preconditionsMet(this)) {
                        actions.add(goldMoveAction);
                    }
                    PickupGoldAction getGoldAction = new PickupGoldAction(p, resource);
                    if (getGoldAction.preconditionsMet(this)) {
                        actions.add(getGoldAction);
                    }
                    break;
                case TREE:
                    MoveToWoodAction woodMoveAction = new MoveToWoodAction(p, resource);
                    if (woodMoveAction.preconditionsMet(this)) {
                        actions.add(woodMoveAction);
                    }
                    PickupWoodAction getWoodAction = new PickupWoodAction(p, resource);
                    if (getWoodAction.preconditionsMet(this)) {
                        actions.add(getWoodAction);
                    }
                    break;
            }
        }
        MoveToTownhallAction townhallMoveAction = new MoveToTownhallAction(p, townHall);
        if (townhallMoveAction.preconditionsMet(this)) {
            actions.add(townhallMoveAction);
        }
        DepositGoldAction putGoldAction = new DepositGoldAction(p, townHall);
        if (putGoldAction.preconditionsMet(this)) {
            actions.add(putGoldAction);
        }
        DepositWoodAction putWoodAction = new DepositWoodAction(p, townHall);
        if (putWoodAction.preconditionsMet(this)) {
            actions.add(putWoodAction);
        }
        return actions;
    }

    /**
     * Write your heuristic function here. Remember this must be admissible for the properties of A* to hold. If you
     * can come up with an easy way of computing a consistent heuristic that is even better, but not strictly necessary.
     * <p>
     * Add a description here in your submission explaining your heuristic.
     *
     * @return The value estimated remaining cost to reach a goal state from this state.
     */
    public double heuristic() {
        double heuristic = 0;

        int remainingWood = townHall.requiredTotalWood - townHall.getCurrentWood();
        int remainingGold = townHall.requiredTotalGold - townHall.getCurrentGold();

        int nonPriorityValue = 25;
        int priorityValue = 50;

        boolean prioritizeGold = townHall.getCurrentGold() - townHall.getCurrentWood() < 500;

        for(Peasant p : peasants.values()){
            if(remainingGold > 0) {
                if (p.isAdjacentGoldSource()) {
                    heuristic += prioritizeGold?priorityValue:nonPriorityValue;
                    if (p.isHoldingGold()) {
                        heuristic += prioritizeGold?priorityValue:nonPriorityValue;
                    }
                }
            }
            if(remainingWood > 0) {
                if (p.isAdjacentWoodSource()) {
                    heuristic += prioritizeGold?nonPriorityValue:priorityValue;
                    if (p.isHoldingWood()) {
                        heuristic += prioritizeGold?nonPriorityValue:priorityValue;
                    }
                }
            }
            if(p.isAdjacentTownHall()){
                if(p.isHoldingWood()){
                    heuristic += prioritizeGold?nonPriorityValue:priorityValue;
                } else if (p.isHoldingGold()){
                    heuristic += prioritizeGold?priorityValue:nonPriorityValue;
                }
            }
        }

        int totalRemainingResources = remainingGold + remainingWood;
        heuristic -= totalRemainingResources / (peasants.size());

//        List<Resource> goldMines = resources.values().stream()
//                //Filter so that only gold mines with resources left are included
//                .filter(res -> res.type == ResourceNode.Type.GOLD_MINE && res.amountRemaining > 0)
//                //Sort by distance to the town hall
//                .sorted((r1, r2) -> Double.compare(r1.position.euclideanDistance(townHall.pos), r2.position.euclideanDistance(townHall.pos)))
//                .collect(Collectors.toList());
//        Resource nearestMine = goldMines.get(0);
//
//        List<Resource> trees = resources.values().stream()
//                .filter(res -> res.type == ResourceNode.Type.TREE && res.amountRemaining > 0)
//                .sorted((r1, r2) -> Double.compare(r1.position.euclideanDistance(townHall.pos), r2.position.euclideanDistance(townHall.pos)))
//                .collect(Collectors.toList());
//        Resource nearestTree = trees.get(0);
//
//        //Compute the round trip time total for each resource.
//        heuristic += Math.max(remainingGold,0)/(100*peasants.size())*(townHall.pos.euclideanDistance(nearestMine.position) + 2);
//        heuristic += Math.max(remainingWood,0)/(100*peasants.size())*(townHall.pos.euclideanDistance(nearestTree.position) + 2);
//
//        for(Peasant p : peasants.values()){
//            //Encourage anyone holding stuff to go turn it in.
//            if(p.isAdjacentTownHall()){
//                if(p.isHoldingGold()){
//                    heuristic -= (p.getPosition().euclideanDistance(townHall.pos) + 1);
//                } else if (p.isHoldingWood()){
//                    heuristic -= (p.getPosition().euclideanDistance(townHall.pos) + 1);
//                }
//            }
//            double treeDist = 0, mineDist = 0;
//            if(p.isAdjacentWoodSource()){
//                treeDist = townHall.pos.euclideanDistance(nearestTree.position);
//            }
//            if(p.isAdjacentGoldSource()){
//                mineDist = townHall.pos.euclideanDistance(nearestMine.position);
//            }
//            double nearestResourceDist = 0;
//            if(remainingGold > 0 && remainingWood > 0){
//                nearestResourceDist = Math.max(treeDist,mineDist);
//            } else if (remainingGold > 0){
//                nearestResourceDist = mineDist;
//            } else if (remainingWood > 0){
//                nearestResourceDist = treeDist;
//            }
//            heuristic -= nearestResourceDist;
//        }

        return heuristic;

    }

    public double queueVal(){
        return getCost() + heuristic();
    }

    /**
     * Write the function that computes the current costToGetHere to get to this node. This is combined with your heuristic to
     * determine which actions/states are better to explore.
     *
     * @return The current costToGetHere to reach this goal
     */
    public double getCost() {
        return costToGetHere;
    }

    public void incrementCost(double incr) {
        costToGetHere += incr;
    }

    public void addAction(StripsAction action) {
        actions.add(action);
    }

    /**
     * This is necessary to use your state in the Java priority queue. See the official priority queue and Comparable
     * interface documentation to learn how this function should work.
     *
     * @param o The other game state to compare
     * @return 1 if this state costs more than the other, 0 if equal, -1 otherwise
     */
    @Override
    public int compareTo(GameState o) {
        return Double.compare(heuristic()-getCost(),o.heuristic()- o.getCost());
    }

    /**
     * This will be necessary to use the GameState as a key in a Set or Map.
     *
     * @param o The game state to compare
     * @return True if this state equals the other state, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameState gameState = (GameState) o;

        if (!townHall.equals(gameState.townHall)) return false;
        if (!peasants.equals(gameState.peasants)) return false;
        return resources.equals(gameState.resources);

    }

    /**
     * This is necessary to use the GameState as a key in a HashSet or HashMap. Remember that if two objects are
     * equal they should hash to the same value.
     *
     * @return An integer hashcode that is equal for equal states.
     */
    @Override
    public int hashCode() {
        int result = townHall.hashCode();
        result = 31 * result + peasants.hashCode();
        result = 31 * result + resources.hashCode();
        return result;
    }

    public GameState apply(StripsAction action) {
        return action.apply(this);
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.00");
        return "GameState Q: " + df.format(queueVal()) + " C: " + df.format(getCost()) + " H: " + df.format(heuristic());
    }
}
