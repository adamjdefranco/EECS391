package edu.cwru.sepia.agent.minimax;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.action.DirectedAction;
import edu.cwru.sepia.action.TargetedAction;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Unit;
import edu.cwru.sepia.util.Direction;

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

    public class BetterUnit {
        public final int id;
        public int x;
        public int y;
        public int range;
        public int health;
        public int player;
        public int damage;

        public BetterUnit(Unit.UnitView unit) {
            this.id = unit.getID();
            this.x = unit.getXPosition();
            this.y = unit.getYPosition();
            this.range = unit.getTemplateView().getRange();
            this.health = unit.getHP();
            this.player = unit.getTemplateView().getPlayer();
            this.damage = unit.getTemplateView().getBasicAttack();
        }

        public boolean canAttack(BetterUnit unit) {
            return //Not on the same team
                    unit.player != this.player &&
                            //Both units are alive
                            this.health > 0 && unit.health > 0 &&
                            //Either they are aligned on the x or y axis and in range
                            ((Math.abs(this.x - unit.x) < this.range && this.y == unit.y && !isObstructedX(this.y, this.x, unit.x))
                                    ||
                                    (Math.abs(this.y - unit.y) < this.range && this.x == unit.x && !isObstructedY(this.x, this.y, unit.y)));
        }

        public void doAttack(BetterUnit unit) {
            unit.health = Math.min(0, unit.health - this.damage);
        }

        public void move(int dx, int dy) {
            x += dx;
            y += dy;
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
            String loc = resourceView.getXPosition()+" "+resourceView.getYPosition();
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
        return 0.0;
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
            //All the valid actions directions for footman 1
            for(Action footman1Action : validActionsForUnit(footman1)){
                for(Action footman2Action : validActionsForUnit(footman2)){
                    Map<Integer,Action> actions = new HashMap<>();
                    actions.put(footman1.id,footman1Action);
                    actions.put(footman2.id, footman2Action);
                    GameStateChild child = childFromStateWithAction(view,actions);
                    children.add(child);
                }
            }
        } else {
            //It's the archer's turns. Generate moves for the archers.
            for(Action archer1Action : validActionsForUnit(archer1)){
                for(Action archer2Action : validActionsForUnit(archer2)){
                    Map<Integer,Action> actions = new HashMap<>();
                    actions.put(archer1.id,archer1Action);
                    actions.put(archer2.id, archer2Action);
                    GameStateChild child = childFromStateWithAction(view,actions);
                    children.add(child);
                }
            }
        }
        return children;
    }

    private List<Action> validActionsForUnit(BetterUnit unit){
        List<Action> actions = new ArrayList<>();

        //Get all valid action movements
        for(Direction d : Direction.values()){
            if(isValidMoveDirection(d) && validMoveInDirection(unit.x, unit.y,d)){
                DirectedAction moveAction = new DirectedAction(unit.id, ActionType.PRIMITIVEMOVE,d);
                actions.add(moveAction);
            }
        }

        //Check and see if you can attack any enemies
        for(Integer id : enemyUnitIds){
            BetterUnit enemy = allUnits.get(id);
            if(unit.canAttack(enemy)){
                TargetedAction attackAction = new TargetedAction(unit.id,ActionType.PRIMITIVEATTACK,id);
                actions.add(attackAction);
            }
        }
        return actions;
    }

    private boolean validMoveInDirection(int x, int y, Direction dir){
        int newX = x + dir.xComponent();
        int newY = y + dir.yComponent();
        //Check to make sure that coordinate is in the map
        //Check x coordinate
        if(newX < 0 || newX >= mapX){
            return false;
        }
        if(newY < 0 || newY >= mapY){
            return false;
        }

        //Check against other units
        for(BetterUnit unit : allUnits.values()){
            if(unit.x == newX && unit.y == newY){
                return false;
            }
        }

        //Check to make sure its not obstructed by obstacles
        if(resourceAtLocation(x,y)){
            return false;
        }
        return false;
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
            }
        }
        enemyUnitIds = this.view.getAllUnitIds().stream().filter(integer -> allUnits.get(integer).player != myPlayerID).collect(Collectors.toList());
        if (enemyUnitIds.size() > 0) {
            archer1 = allUnits.get(enemyUnitIds.get(0));
            if(enemyUnitIds.size() > 1){
                archer2 = allUnits.get(enemyUnitIds.get(1));
            }
        }

    }

    private boolean isObstructedX(int baseY, int pos, int goal) {
        int min = Math.min(pos, goal);
        int max = Math.min(pos, goal);
        for(int i=min; i<=max; i++){
            String str = i + " " + baseY;
            if(takenResourceLocations.contains(str)){
                return true;
            }
        }
        return false;
    }

    private boolean isObstructedY(int baseX, int pos, int goal) {
        int min = Math.min(pos,goal);
        int max = Math.max(pos,goal);
        for(int i=min; i<=max; i++){
            if(resourceAtLocation(baseX, i)){
                return true;
            }
        }
        return false;
    }

    private boolean resourceAtLocation(int x, int y){
        String str = x + " " + y;
        return takenResourceLocations.contains(str);
    }

    private GameStateChild childFromStateWithAction(State.StateView state, Map<Integer, Action> unitActions) {
        GameState g = new GameState(state);
        g.computeUnitLists(this.myPlayerID);
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

}
