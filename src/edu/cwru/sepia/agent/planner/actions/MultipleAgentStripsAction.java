package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;

import java.util.ArrayList;
import java.util.List;

// Class to check if the preconditions for multiple actions are met. Also can apply the multiple actions to a state.
public class MultipleAgentStripsAction implements StripsAction {

    public final List<StripsAction> actions;

    public MultipleAgentStripsAction(List<StripsAction> actions) {
        this.actions = new ArrayList<>(actions);
    }

    // Checks if all of the preconditions are met for multiple agents
    @Override
    public boolean preconditionsMet(GameState state) {
        for(StripsAction action : actions){
            if(!action.preconditionsMet(state)){
                return false;
            }
        }
        return true;
    }

    // Applies multiple actions to a given state
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
