package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.TownHall;

/**
 * Created by james on 3/28/16.
 */
public class PickupWoodAction implements StripsAction {

    final Peasant peasant;
    final TownHall townHall;

    public PickupWoodAction(Peasant peasant, TownHall townHall) {
        this.peasant = peasant;
        this.townHall = townHall;
    }

    //public PickupWoodAction(int unitID){

    //}

    @Override
    public boolean preconditionsMet(GameState state) {
        return !(peasant.isHoldingWood()) && peasant.isAdjacentWoodSource();
    }

    @Override
    public GameState apply(GameState state) {
        peasant.setHoldingWood(true);
        return null;
    }
}
