package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by james on 3/31/16.
 */
public class MultipleAgentStripsAction implements StripsAction {

    public final List<StripsAction> actions;

    public MultipleAgentStripsAction(List<StripsAction> actions) {
        this.actions = new ArrayList<>(actions);
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        for(StripsAction action : actions){
            if(!action.preconditionsMet(state)){
                return false;
            }
        }
        return true;
    }

    @Override
    public GameState apply(GameState state) {
        GameState clone = new GameState(state);
        for(StripsAction action : actions){
            clone = action.apply(clone);
        }
        return clone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(StripsAction a : actions){
            sb.append(a.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
