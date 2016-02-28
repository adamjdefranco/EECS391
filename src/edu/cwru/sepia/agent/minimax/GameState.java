package edu.cwru.sepia.agent.minimax;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.environment.model.state.ResourceNode;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Unit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    List<ResourceNode.ResourceView> allObstacles;
    State.StateView view;
    List<Unit.UnitView> allUnits;
    BetterUnit footman1;
    BetterUnit footman2;
    BetterUnit archer1;
    BetterUnit archer2;

    public class BetterUnit {
        public int x;
        public int y;
        public int range;
        public int health;
        public int player;

        public BetterUnit(Unit.UnitView unit) {
            this.x = unit.getXPosition();
            this.y = unit.getYPosition();
            this.range = unit.getTemplateView().getRange();
            this.health = unit.getHP();
            this.player = unit.getTemplateView().getPlayer();
        }

        public boolean willAttack(BetterUnit unit) {
            return //Not on the same team
                    unit.player != this.player &&
                            //Both units are alive
                            this.health > 0 && unit.health > 0 &&
                            //Either they are aligned on the x or y axis and in range
                            ((Math.abs(this.x - unit.x) < this.range && this.y == unit.y)
                                    ||
                                    (Math.abs(this.y - unit.y) < this.range && this.x == unit.x));
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
        allUnits = state.getAllUnits();
        mapX = state.getXExtent();
        mapY = state.getYExtent();
        allObstacles = state.getAllResourceNodes();
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
        return children;
    }

}
