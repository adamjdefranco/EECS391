package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;

/**
 * Created by james on 3/28/16.
 */
public class PickupWoodAction implements StripsAction {

    public PickupWoodAction(int unitID){

    }

    @Override
    public boolean preconditionsMet(GameState state) {
        return false;
    }

    @Override
    public GameState apply(GameState state) {
        return null;
    }
}
