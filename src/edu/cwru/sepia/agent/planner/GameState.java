package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Unit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is used to represent the state of the game after applying one of the available actions. It will also
 * track the A* specific information such as the parent pointer and the cost and heuristic function. Remember that
 * unlike the path planning A* from the first assignment the cost of an action may be more than 1. Specifically the cost
 * of executing a compound action such as move can be more than 1. You will need to account for this in your heuristic
 * and your cost function.
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
    public Map<Integer, ResourceNode.ResourceView> resources;

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
        resources = state.getAllResourceIds().stream().map(state::getResourceNode).collect(Collectors.toMap(ResourceNode.ResourceView::getID, c->c));
        for (Unit.UnitView unit : state.getUnitIds(playernum).stream().map(state::getUnit).collect(Collectors.toList())) {
            String unitType = unit.getTemplateView().getName().toLowerCase();
            if (unitType.equals("townhall")) {
                this.townHall = new TownHall(unit.getID(),
                        Position.forUnit(unit),
                        requiredGold,
                        requiredWood,
                        buildPeasants);
                this.peasants.values().stream().filter(p->p.getPosition().isAdjacent(townHall.pos)).forEach(peasant -> peasant.setAdjacentTownHall(true));
            } else if (unitType.equals("peasant")) {
                Peasant p = new Peasant(unit.getID());
                p.setPosition(new Position(unit.getXPosition(), unit.getYPosition()));
                peasants.put(p.id, p);
                if(this.townHall != null && p.getPosition().isAdjacent(townHall.pos)){
                    p.setAdjacentTownHall(true);
                }
                for(ResourceNode.ResourceView resource : resources.values()){
                    Position resourcePos = Position.forResource(resource);
                    if(p.isAdjacentGoldSource() && p.isAdjacentTownHall() && p.isAdjacentWoodSource()){
                        //No point continuing to search to see if the unit is adjacent to things... this probably wont be triggered.
                        break;
                    }
                    if(resource.getType() == ResourceNode.Type.GOLD_MINE && !p.isAdjacentGoldSource() && p.getPosition().isAdjacent(resourcePos)){
                        p.setAdjacentGoldSource(true);
                    }
                    if(resource.getType() == ResourceNode.Type.TREE && !p.isAdjacentWoodSource() && p.getPosition().isAdjacent(resourcePos)){
                        p.setAdjacentGoldSource(true);
                    }
                }
                //TODO probably initialize other things here.
            }
        }
    }

    /**
     * Unlike in the first A* assignment there are many possible goal states. As long as the wood and gold requirements
     * are met the peasants can be at any location and the capacities of the resource locations can be anything. Use
     * this function to check if the goal conditions are met and return true if they are.
     *
     * @return true if the goal conditions are met in this instance of game state.
     */
    public boolean isGoal() {
        return townHall.getCurrentGold() == townHall.requiredTotalGold && townHall.getCurrentWood() == townHall.requiredTotalWood;
    }

    /**
     * The branching factor of this search graph are much higher than the planning. Generate all of the possible
     * successor states and their associated actions in this method.
     *
     * @return A list of the possible successor states and their associated actions
     */
    public List<GameState> generateChildren() {
        // TODO: Implement me!
        return null;
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
        // TODO: Implement me!
        return 0.0;
    }

    /**
     * Write the function that computes the current cost to get to this node. This is combined with your heuristic to
     * determine which actions/states are better to explore.
     *
     * @return The current cost to reach this goal
     */
    public double getCost() {
        // TODO: Implement me!
        return 0.0;
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
        // TODO: Implement me!
        return 0;
    }

    /**
     * This will be necessary to use the GameState as a key in a Set or Map.
     *
     * @param o The game state to compare
     * @return True if this state equals the other state, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        // TODO: Implement me!
        return false;
    }

    /**
     * This is necessary to use the GameState as a key in a HashSet or HashMap. Remember that if two objects are
     * equal they should hash to the same value.
     *
     * @return An integer hashcode that is equal for equal states.
     */
    @Override
    public int hashCode() {
        // TODO: Implement me!
        return 0;
    }
}
