package edu.cwru.sepia.agent.planner.actions;

import edu.cwru.sepia.agent.planner.GameState;
import edu.cwru.sepia.agent.planner.Peasant;
import edu.cwru.sepia.agent.planner.TownHall;

/**
 * Created by james on 3/28/16.
 */
public class DepositGoldAction implements StripsAction {

    final Peasant peasant;
    final TownHall townHall;

    public DepositGoldAction(Peasant peasant, TownHall townHall) {
        this.peasant = peasant;
        this.townHall = townHall;
    }

    @Override
    public boolean preconditionsMet(GameState state) {
        return peasant.isHoldingGold() && peasant.isAdjacentTownHall();
    }

    @Override
    public GameState apply(GameState state) {
        peasant.setHoldingGold(false);
        // Do we need to do townHall.HasWood() or something of the sort?
        // TODO: How do we update the gamestate to reflect this?
        return null;
    }
}
