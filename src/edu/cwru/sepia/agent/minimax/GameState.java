package edu.cwru.sepia.agent.minimax;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.DirectedAction;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Unit;
import edu.cwru.sepia.util.Direction;
import edu.cwru.sepia.util.Pair;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class stores all of the information the agent
 * needs to know about the state of the game. For example this
 * might include things like footmen HP and positions.
 * <p>
 * Add any information or methods you would like to this class,
 * but do not delete or change the signatures of the provided methods.
 */
public class GameState {

    int mapX;
    int mapY;
    Set<String> takenResourceLocations = new HashSet<>();
    Map<Integer, BetterUnit> allUnits;
    public State.StateView view;
    BetterUnit footman1;
    BetterUnit footman2;
    BetterUnit archer1;
    BetterUnit archer2;
    private int myPlayerID = -1;
    private List<Integer> myUnitIds;
    private List<Integer> enemyUnitIds;
    MapLocation aStarLocation1;
    MapLocation aStarLocation2;

    public class BetterUnit {
        public final int id;
        public int x;
        public int y;
        public int range;
        public int health;
        public int player;
        public int damage;
        public int maxHealth;
        public int healthPercent;

        public BetterUnit(Unit.UnitView unit) {
            this.id = unit.getID();
            this.x = unit.getXPosition();
            this.y = unit.getYPosition();
            this.range = unit.getTemplateView().getRange();
            this.health = unit.getHP();
            this.player = unit.getTemplateView().getPlayer();
            this.damage = unit.getTemplateView().getBasicAttack();
            this.maxHealth = unit.getTemplateView().getBaseHealth();
            this.healthPercent = ((int)(((float)health)/(float)maxHealth));
        }

        public BetterUnit(){
            this.id = -1;
            this.x = -1;
            this.y = -1;
            this.range = 0;
            this.health = 0;
            this.player = -1;
            this.damage = 0;
            this.maxHealth = 0;
            this.healthPercent = 0;
        }

        public MapLocation getMapLocation() {
            return new MapLocation(this.x, this.y, null);
        }

        public boolean canAttack(BetterUnit unit) {
            return //Both units are alive
                    this.health > 0 && unit.health > 0 &&
                    //Not on the same team
                    unit.player != this.player &&
                            //Either they are aligned on the x or y axis and in range
                            ((Math.abs(this.x - unit.x) <= this.range)
                                    &&
                                    (Math.abs(this.y - unit.y) <= this.range));
        }

        public void doAttack(BetterUnit unit) {
            if(this.isAlive()) {
                unit.health = Math.min(0, unit.health - this.damage);
            }
        }

        public void move(int dx, int dy) {
            if(this.isAlive()) {
                x += dx;
                y += dy;
            }
        }

        public boolean isAlive() {
            return this.health > 0;
        }
    }

    //Boolean for tracking if it is my turn or the enemy agents. Is set externally in the a-b search
    public boolean isMyTurn = true;

    /**
     * You will implement this constructor. It will
     * extract all of the needed state information from the built in
     * SEPIA state view.
     * <p>
     * You may find the following state methods useful:
     * <p>
     * state.getXExtent() and state.getYExtent(): get the map dimensions
     * state.getAllResourceIDs(): returns all of the obstacles in the map
     * state.getResourceNode(Integer resourceID): Return a ResourceView for the given ID
     * <p>
     * For a given ResourceView you can query the position using
     * resource.getXPosition() and resource.getYPosition()
     * <p>
     * For a given unit you will need to find the attack damage, range and max HP
     * unitView.getTemplateView().getRange(): This gives you the attack range
     * unitView.getTemplateView().getBasicAttack(): The amount of damage this unit deals
     * unitView.getTemplateView().getBaseHealth(): The maximum amount of health of this unit
     *
     * @param state Current state of the episode
     */
    public GameState(State.StateView state) {
        allUnits = state.getAllUnits().stream().map(BetterUnit::new).collect(Collectors.toMap((Function<BetterUnit, Integer>) betterUnit -> betterUnit.id, (Function<BetterUnit, BetterUnit>) betterUnit -> betterUnit));
        mapX = state.getXExtent();
        mapY = state.getYExtent();
        state.getAllResourceNodes().stream().forEach(resourceView -> {
            String loc = resourceView.getXPosition() + " " + resourceView.getYPosition();
            takenResourceLocations.add(loc);
        });
        this.view = state;
    }

    /**
     * You will implement this function.
     * <p>
     * You should use weighted linear combination of features.
     * The features may be primitives from the state (such as hp of a unit)
     * or they may be higher level summaries of information from the state such
     * as distance to a specific location. Come up with whatever features you think
     * are useful and weight them appropriately.
     * <p>
     * It is recommended that you start simple until you have your algorithm working. Then watch
     * your agent play and try to add features that correct mistakes it makes. However, remember that
     * your features should be as fast as possible to compute. If the features are slow then you will be
     * able to do less plys in a turn.
     * <p>
     * Add a good comment about what is in your utility and why you chose those features.
     *
     * @return The weighted linear combination of the features
     */
    public double getUtility() {
        double utility = 0.0;

        if(footman1.isAlive()) {
            // Edits utility based on footman1's ability to attack, health, and distance from archer1
            if((archer1.isAlive() && footman1.canAttack(archer1)) || (archer2.isAlive() && footman1.canAttack(archer2))) {
                utility += 500;
            }
            if (archer1.isAlive()) {
                utility -= getDistanceBetweenUnits(footman1, archer1);
            }
            if (archer2.isAlive()) {
                utility -= getDistanceBetweenUnits(footman1, archer2);
            }
            utility += footman1.healthPercent;
        }
        // Edits utility based on footman2's ability to attack, health, and distance from archer2
        if(footman2.isAlive()) {
            if(footman2.canAttack(archer1) || (archer2.isAlive() && footman2.canAttack(archer2))) {
                utility += 500;
            }
            if(archer1.isAlive()) {
                utility -= getDistanceBetweenUnits(footman2, archer1);
            }
            if(archer2.isAlive()) {
                utility -= getDistanceBetweenUnits(footman2, archer2);
            }
            utility += footman2.healthPercent;
        }

        // Edits utility based on archer1's ability to attack and health
        if(archer1.isAlive()) {
            if((footman1.isAlive() && archer1.canAttack(footman1)) || (footman2.isAlive() && archer1.canAttack(footman2))) {
                utility -= 500;
            }
            utility += ((float)(archer1.maxHealth - archer1.health)/archer1.maxHealth)*100;
        } else {
            utility += 1000;
        }

        // Edits utility based on archer1's ability to attack and health
        if(archer2.isAlive()) {
            if((footman2.isAlive() && archer2.canAttack(footman2)) || (footman1.isAlive() && archer2.canAttack(footman1))) {
                utility -= 500;
            }
            utility += ((float)(archer2.maxHealth - archer2.health)/archer2.maxHealth)*100;
        } else {
            utility += 1000;
        }

        return utility;
    }

    // Computes the distance between two units keeping in mind that they cannot move horizontally
    public double getDistanceBetweenUnits(BetterUnit unit1, BetterUnit unit2) {
        double xDist = Math.abs(unit1.x - unit2.x);
        double yDist = Math.abs(unit1.y - unit2.y);
        return xDist + yDist;
    }

    /**
     * You will implement this function.
     * <p>
     * This will return a list of GameStateChild objects. You will generate all of the possible
     * actions in a step and then determine the resulting game state from that action. These are your GameStateChildren.
     * <p>
     * You may find it useful to iterate over all the different directions in SEPIA.
     * <p>
     * for(Direction direction : Directions.values())
     * <p>
     * To get the resulting position from a move in that direction you can do the following
     * x += direction.xComponent()
     * y += direction.yComponent()
     *
     * @return All possible actions and their associated resulting game state
     */
    public List<GameStateChild> getChildren() {
        List<GameStateChild> children = new ArrayList<>();
        if (isMyTurn) {
            //It's my turn. Generate new moves for the footmen
            //Check and see if both footman are alive. If so, compute all joint actions.
            if (footman1 != null && footman2 != null && footman1.isAlive() && footman2.isAlive()) {
                //All the valid actions directions for footman 1
                for (Action footman1Action : validActionsForUnit(footman1)) {
                    for (Action footman2Action : validActionsForUnit(footman2)) {
                        Map<Integer, Action> actions = new HashMap<>();
                        actions.put(footman1.id, footman1Action);
                        actions.put(footman2.id, footman2Action);
                        GameStateChild child = childFromStateWithAction(view, actions);
                        children.add(child);
                    }
                }
            } else if (footman1 != null && footman1.isAlive()) {
                //All the valid actions directions for footman 1
                for (Action footman1Action : validActionsForUnit(footman1)) {
                    Map<Integer, Action> actions = new HashMap<>();
                    actions.put(footman1.id, footman1Action);
                    GameStateChild child = childFromStateWithAction(view, actions);
                    children.add(child);
                }
            } else if (footman2 != null && footman2.isAlive()) {
                for (Action footman2Action : validActionsForUnit(footman2)) {
                    Map<Integer, Action> actions = new HashMap<>();
                    actions.put(footman2.id, footman2Action);
                    GameStateChild child = childFromStateWithAction(view, actions);
                    children.add(child);
                }
            }
        } else {
            //It's the archer's turns. Generate moves for the archers.
            //Check and see if both the archers are available and alive. If so, generate all moves for both
            if (archer1.isAlive() && archer2.isAlive()) {
                for (Action archer1Action : validActionsForUnit(archer1)) {
                    for (Action archer2Action : validActionsForUnit(archer2)) {
                        Map<Integer, Action> actions = new HashMap<>();
                        actions.put(archer1.id, archer1Action);
                        actions.put(archer2.id, archer2Action);
                        GameStateChild child = childFromStateWithAction(view, actions);
                        children.add(child);
                    }
                }
            } else if (archer1.isAlive()) {
                for (Action archer1Action : validActionsForUnit(archer1)) {
                    Map<Integer, Action> actions = new HashMap<>();
                    actions.put(archer1.id, archer1Action);
                    GameStateChild child = childFromStateWithAction(view, actions);
                    children.add(child);
                }
            } else if (archer2.isAlive()) {
                for (Action archer2Action : validActionsForUnit(archer2)) {
                    Map<Integer, Action> actions = new HashMap<>();
                    actions.put(archer2.id, archer2Action);
                    GameStateChild child = childFromStateWithAction(view, actions);
                    children.add(child);
                }
            }
        }
        return children;
    }

    public void getAStarPaths() {
        if(archer1.isAlive() && footman1.isAlive()) {
            aStarLocation1 = AstarSearch(new MapLocation(footman1.x, footman1.y, null, 0), new MapLocation(archer1.x, archer1.y, null, 0), mapX, mapY).pop();
        }
        if(archer1.isAlive() && footman2.isAlive()) {
            aStarLocation2 = AstarSearch(new MapLocation(footman1.x, footman1.y, null, 0), new MapLocation(archer1.x, archer1.y, null, 0), mapX, mapY).pop();
        }
    }

    private List<Action> validActionsForUnit(BetterUnit unit) {
        List<Action> actions = new ArrayList<>();

        //Get all valid action movements
        for (Direction d : Direction.values()) {
            if (isValidMoveDirection(d) && validMoveInDirection(unit.x, unit.y, d)) {
                DirectedAction moveAction = new DirectedAction(unit.id, ActionType.PRIMITIVEMOVE, d);
                actions.add(moveAction);
            }
        }

        //Check and see if you can attack any enemies
        for (Integer id : enemyUnitIds) {
            BetterUnit enemy = allUnits.get(id);
            if (unit.canAttack(enemy)) {
                TargetedAction attackAction = new TargetedAction(unit.id, ActionType.PRIMITIVEATTACK, enemy.id);
                actions.add(attackAction);
            }
        }
        return actions;
    }

    private boolean validMoveInDirection(int x, int y, Direction dir) {
        int newX = x + dir.xComponent();
        int newY = y + dir.yComponent();
        //Check to make sure that coordinate is in the map
        //Check x coordinate
        if (newX < 0 || newX >= mapX) {
            return false;
        }
        if (newY < 0 || newY >= mapY) {
            return false;
        }

        //Check against other units
        for (BetterUnit unit : allUnits.values()) {
            if (unit.x == newX && unit.y == newY) {
                return false;
            }
        }

        //Check to make sure its not obstructed by obstacles
        if (resourceAtLocation(x, y)) {
            return false;
        }
        return true;
    }

    private static boolean isValidMoveDirection(Direction d) {
        return d.equals(Direction.NORTH) || d.equals(Direction.SOUTH) || d.equals(Direction.EAST) || d.equals(Direction.WEST);
    }

    public void computeUnitLists(int myPlayerID) {
        this.myPlayerID = myPlayerID;
        myUnitIds = this.view.getUnitIds(myPlayerID);
        if (myUnitIds.size() > 0) {
            footman1 = allUnits.get(myUnitIds.get(0));
            if (myUnitIds.size() > 1) {
                footman2 = allUnits.get(myUnitIds.get(1));
            } else {
                footman2 = new BetterUnit();
            }
        } else {
            footman1 = new BetterUnit();
            footman2 = new BetterUnit();
        }
        enemyUnitIds = this.view.getAllUnitIds().stream().filter(integer -> allUnits.get(integer).player != myPlayerID).collect(Collectors.toList());
        if (enemyUnitIds.size() > 0) {
            archer1 = allUnits.get(enemyUnitIds.get(0));
            if (enemyUnitIds.size() > 1) {
                archer2 = allUnits.get(enemyUnitIds.get(1));
            } else {
                archer2 = new BetterUnit();
            }
        } else {
            archer1 = new BetterUnit();
            archer2 = new BetterUnit();
        }

        getAStarPaths();
    }

    private boolean isObstructedX(int baseY, int pos, int goal) {
        int min = Math.min(pos, goal);
        int max = Math.min(pos, goal);
        for (int i = min; i < max; i++) {
            if(resourceAtLocation(i,baseY)){
                return true;
            }
        }
        return false;
    }

    private boolean isObstructedY(int baseX, int pos, int goal) {
        int min = Math.min(pos, goal);
        int max = Math.max(pos, goal);
        for (int i = min; i < max; i++) {
            if (resourceAtLocation(baseX, i)) {
                return true;
            }
        }
        return false;
    }

    private boolean resourceAtLocation(int x, int y) {
        String str = x + " " + y;
        return takenResourceLocations.contains(str);
    }

    private GameStateChild childFromStateWithAction(State.StateView state, Map<Integer, Action> unitActions) {
        GameState g = new GameState(state);
        g.computeUnitLists(this.myPlayerID);
        g.isMyTurn = !isMyTurn;
        GameStateChild newChild = new GameStateChild(unitActions, g);
        unitActions.forEach((integer, action) -> {
            BetterUnit unit = newChild.state.allUnits.get(integer);
            if (action.getType() == ActionType.PRIMITIVEMOVE) {
                DirectedAction actualAction = (DirectedAction) action;
                unit.move(actualAction.getDirection().xComponent(), actualAction.getDirection().yComponent());
            } else if (action.getType() == ActionType.PRIMITIVEATTACK) {
                TargetedAction actualAction = (TargetedAction) action;
                BetterUnit target = newChild.state.allUnits.get(actualAction.getTargetId());
                BetterUnit attacker = newChild.state.allUnits.get(actualAction.getUnitId());
                attacker.doAttack(target);
            } else {
                //Do nothing - this state should never be seen.
            }
        });
        return newChild;
    }

    public boolean allMyUnitsDead() {
        for (Integer id : myUnitIds) {
            BetterUnit unit = allUnits.get(id);
            if (unit.health > 0) {
                return false;
            }
        }
        return true;
    }

    public boolean allEnemyUnitsDead() {
        for (Integer id : enemyUnitIds) {
            BetterUnit unit = allUnits.get(id);
            if (unit.health > 0) {
                return false;
            }
        }
        return true;
    }

    class MapLocation {
        public int x, y;
        public MapLocation previous;
        public float cost;
        public float heuristic;

        public MapLocation(int x, int y, MapLocation cameFrom, float cost) {
            this.x = x;
            this.y = y;
            this.previous = cameFrom;
            this.cost = cost;
        }

        public void setHeuristic(MapLocation goalState) {
            this.heuristic = chebyshevDistance(this, goalState);
        }

        // Convenience constructor used in getNeighbors for adding 1 to the cost
        public MapLocation(int x, int y, MapLocation cameFrom){
            this(x,y,cameFrom,cameFrom.cost+1);
        }

        // Checks if two MapLocations are the same
        public boolean equals(Object loc){
            if(loc instanceof MapLocation){
                MapLocation ml = (MapLocation) loc;
                return x == ml.x && y == ml.y;
            } else {
                return false;
            }
        }

        // Writes out the x and y coordinates, used for debugging and hashcode
        public String toString(){
            return "("+x+","+y+")";
        }

        // Returns a list of all of the neighbor map locations of this's maplocation
        public List<MapLocation> getNeighbours(int xExtent, int yExtent) {
            List<MapLocation> mapLocs = new ArrayList<>();
            if(x-1>=0){
                //WEST
                mapLocs.add(new MapLocation(x-1,y,this));
                if(y-1 >= 0){
                    //NORTHWEST
                    mapLocs.add(new MapLocation(x-1,y-1,this));
                }
                if(y+1 <= yExtent){
                    //NORTHEAST
                    mapLocs.add(new MapLocation(x-1,y+1,this));
                }
            }
            if(y-1 >= 0){
                //NORTH
                mapLocs.add(new MapLocation(x,y-1,this));
            }
            if(y+1 <= yExtent){
                //SOUTH
                mapLocs.add(new MapLocation(x,y+1,this));
            }
            if(x+1 <= xExtent){
                //EAST
                mapLocs.add(new MapLocation(x+1,y,this));
                if(y-1 >= 0){
                    //NORTHEAST
                    mapLocs.add(new MapLocation(x+1,y-1,this));
                }
                if(y+1 <= yExtent){
                    //SOUTHEAST
                    mapLocs.add(new MapLocation(x+1,y+1,this));
                }
            }
            return mapLocs;
        }

        // Function for calculating the Chebyshev Heuristic
        public float chebyshevDistance(MapLocation a, MapLocation b) {
            return Math.max(Math.abs(b.x - a.x), Math.abs(b.y - a.y));
        }

        @Override
        public int hashCode() {
            return toString().hashCode();
        }
    }

    private Stack<MapLocation> AstarSearch(MapLocation start, MapLocation goal, int xExtent, int yExtent) {
        // Priority queue for the open list of nodes
        PriorityQueue<MapLocation> queue = new PriorityQueue<>((o1, o2) -> {
            return Float.compare((o1.cost + o1.heuristic),(o2.cost + o2.heuristic));
        });
        // Set for the closed list of nodes
        Set<MapLocation> closedList = new HashSet<>();
        start.setHeuristic(goal);
        // Adds the initial node to the queue
        queue.add(start);
        MapLocation currentLoc;
        // Runs until you reach the goal node
        do {
            // CurrentLoc becomes the first location in the open list
            currentLoc = queue.poll();
            // Prints if there is no path available and exits the program if so
            if(currentLoc == null) {
                System.out.println("No available path.");
                System.exit(0);
            }
            System.out.println("Looking at location " + currentLoc.toString());
            // Adds the current MapLocation to the closed list
            closedList.add(currentLoc);
            // Instantiates a list of the neighbors of the current location
            List<MapLocation> neighbours = currentLoc.getNeighbours(xExtent, yExtent);
            // Loops through all of the neighbors of the MapLocation m
            for(MapLocation m : neighbours){
                if(archer1.isAlive() && archer1.getMapLocation().equals(m) || (archer2.isAlive() && archer2.getMapLocation().equals(m))){
                    continue;
                }
                // Won't choose to move to location m if there is a resource in the way
                if(resourceAtLocation(m.x, m.y)){
                    System.out.println("Can't go to "+m+" because it is resource-blocked.");
                    continue;
                }
                // Won't choose to move to location m if it is already in the closed list
                if(closedList.contains(m)){
                    continue;
                }
                // Won't choose to move to location m if it is already in the open list
                if(queue.contains(m)){
                    continue;
                }
                // Compute H(x) for the location
                m.setHeuristic(goal);
                System.out.println("Adding "+m+" to queue");
                queue.add(m);
            }
        } while (!currentLoc.equals(goal));
        // Stack for the path for the footman to travel
        Stack<MapLocation> path = new Stack<>();

        // Pushes the path onto the stack backwards so when you pop it off later it will be in order
        while(!currentLoc.equals(start)){
            path.push(currentLoc);
            System.out.println("Go to location " + currentLoc.toString());
            currentLoc = currentLoc.previous;
        }
        return path;
    }

}
