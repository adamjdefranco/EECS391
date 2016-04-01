package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.action.ActionFeedback;
import edu.cwru.sepia.action.ActionResult;
import edu.cwru.sepia.action.ActionType;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.agent.planner.actions.*;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.ResourceType;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Template;
import edu.cwru.sepia.environment.model.state.Unit;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is an outline of the PEAgent. Implement the provided methods. You may add your own methods and members.
 */
public class PEAgent extends Agent {

    // The plan being executed
    private Stack<StripsAction> plan;

    // maps the real unit Ids to the plan's unit ids
    // when you're planning you won't know the true unit IDs that sepia assigns. So you'll use placeholders (1, 2, 3).
    // this maps those placeholders to the actual unit IDs.
    private Map<Integer, Integer> peasantIdMap;
    private Map<Integer, Action> lastIssuedActions;
    private int townhallId;
    private int peasantTemplateId;
    private int requiredWood;
    private int requiredGold;

    public PEAgent(int playernum, Stack<StripsAction> plan) {
        super(playernum);
        peasantIdMap = new HashMap<>();
        this.plan = plan;

    }

    @Override
    public Map<Integer, Action> initialStep(State.StateView stateView, History.HistoryView historyView) {
        // gets the townhall ID and the peasant ID
        for (int unitId : stateView.getUnitIds(playernum)) {
            Unit.UnitView unit = stateView.getUnit(unitId);
            String unitType = unit.getTemplateView().getName().toLowerCase();
            if (unitType.equals("townhall")) {
                townhallId = unitId;
            } else if (unitType.equals("peasant")) {
                peasantIdMap.put(peasantIdMap.size() + 1, unitId);
            }
        }

        // Gets the peasant template ID. This is used when building a new peasant with the townhall
        for (Template.TemplateView templateView : stateView.getTemplates(playernum)) {
            if (templateView.getName().toLowerCase().equals("peasant")) {
                peasantTemplateId = templateView.getID();
                break;
            }
        }

        return middleStep(stateView, historyView);
    }

    /**
     * This is where you will read the provided plan and execute it. If your plan is correct then when the plan is empty
     * the scenario should end with a victory. If the scenario keeps running after you run out of actions to execute
     * then either your plan is incorrect or your execution of the plan has a bug.
     * <p>
     * You can create a SEPIA deposit action with the following method
     * Action.createPrimitiveDeposit(int peasantId, Direction townhallDirection)
     * <p>
     * You can create a SEPIA harvest action with the following method
     * Action.createPrimitiveGather(int peasantId, Direction resourceDirection)
     * <p>
     * You can create a SEPIA build action with the following method
     * Action.createPrimitiveProduction(int townhallId, int peasantTemplateId)
     * <p>
     * You can create a SEPIA move action with the following method
     * Action.createCompoundMove(int peasantId, int x, int y)
     * <p>
     * these actions are stored in a mapping between the peasant unit ID executing the action and the action you created.
     * <p>
     * For the compound actions you will need to check their progress and wait until they are complete before issuing
     * another action for that unit. If you issue an action before the compound action is complete then the peasant
     * will stop what it was doing and begin executing the new action.
     * <p>
     * To check an action's progress you can call getCurrentDurativeAction on each UnitView. If the Action is null nothing
     * is being executed. If the action is not null then you should also call getCurrentDurativeProgress. If the value is less than
     * 1 then the action is still in progress.
     * <p>
     * Also remember to check your plan's preconditions before executing!
     */
    @Override
    public Map<Integer, Action> middleStep(State.StateView stateView, History.HistoryView historyView) {
        GameState currentState = new GameState(stateView, playernum, requiredGold, requiredWood, true);
        System.out.println("Executing Middle Step for turn number: " + stateView.getTurnNumber());
        if (stateView.getTurnNumber() != 0) {
            Map<Integer, ActionResult> actionResults = historyView.getCommandFeedback(playernum, stateView.getTurnNumber() - 1);
            Map<Integer, Action> reissuedActions = new HashMap<>();
            int inProgressActions = 0;
            for (Map.Entry<Integer, ActionResult> unitResults : actionResults.entrySet()) {
                ActionFeedback feedback = unitResults.getValue().getFeedback();
                if (feedback == ActionFeedback.COMPLETED) {
                    if (unitResults.getKey() == townhallId && lastIssuedActions.get(townhallId).getType() == ActionType.PRIMITIVEPRODUCE) {
                        List<Integer> newSepiaUnitIDs = stateView.getUnitIds(playernum).stream().map(i -> stateView.getUnit(i)).filter(u -> u.getTemplateView().getName().toLowerCase().equals("peasant") && !peasantIdMap.containsValue(u.getID())).map(u -> u.getID()).collect(Collectors.toList());
                        for (Integer newID : newSepiaUnitIDs) {
                            peasantIdMap.put(peasantIdMap.size() + 1, newID);
                        }
                    } else if (peasantIdMap.containsValue(unitResults.getKey())) {
                        //Do double checks here of a sort in case Anna was right.
                        if(!unitResults.getValue().getAction().equals(lastIssuedActions.get(unitResults.getKey()))){
                            reissuedActions.put(unitResults.getKey(), lastIssuedActions.get(unitResults.getKey()));
                        }
                    }
                } else {
                    if (feedback == ActionFeedback.FAILED) {
                        reissuedActions.put(unitResults.getKey(), lastIssuedActions.get(unitResults.getKey()));
                    } else if (feedback == ActionFeedback.INCOMPLETE) {
                        inProgressActions++;
                    } else {
                        System.out.println("Unaccounted for action feedback result: " + feedback.toString());
                    }

                }
            }
            if (reissuedActions.size() > 0) {
                return reissuedActions;
            } else if (inProgressActions > 0) {
                return null;
            }
        }
        if (!plan.isEmpty()) {
            StripsAction action = plan.pop();
            if (action.preconditionsMet(currentState)) {
                lastIssuedActions = createSepiaAction(action, stateView);
                //Check command history to make sure unit's are ready to receive commands.
                return lastIssuedActions;
            } else {
                System.err.println("Something is wrong with the plan. Action: " + action.toString());
                return null;
            }
        } else {
            System.out.println("We have exhausted the plan.");
            return null;
        }
    }

    /**
     * Returns a SEPIA version of the specified Strips Action.
     *
     * @param action StripsAction
     * @return SEPIA representation of same action
     */
    private Map<Integer, Action> createSepiaAction(StripsAction action, State.StateView state) {
        Map<Integer, Action> actions = new HashMap<>();
        if (action instanceof MoveToGoldAction) {
            int actualID = peasantIdMap.get(((MoveToGoldAction) action).peasantID);
            Position resourcePos = Position.forResource(state.getResourceNode(((MoveToGoldAction) action).resourceID));
            Action a = Action.createCompoundMove(actualID, resourcePos.x, resourcePos.y);
            actions.put(actualID, a);
        } else if (action instanceof MoveToWoodAction) {
            int actualID = peasantIdMap.get(((MoveToWoodAction) action).peasantID);
            Position resourcePos = Position.forResource(state.getResourceNode(((MoveToWoodAction) action).resourceID));
            Action a = Action.createCompoundMove(actualID, resourcePos.x, resourcePos.y);
            actions.put(actualID, a);
        } else if (action instanceof MoveToTownhallAction) {
            int actualID = peasantIdMap.get(((MoveToTownhallAction) action).peasantID);
            Position resourcePos = Position.forUnit(state.getUnit(((MoveToTownhallAction) action).townHallID));
            Action a = Action.createCompoundMove(actualID, resourcePos.x, resourcePos.y);
            actions.put(actualID, a);
        } else if (action instanceof DepositGoldAction) {
            int actualID = peasantIdMap.get(((DepositGoldAction) action).peasantID);
            Position peasantPosition = Position.forUnit(state.getUnit(actualID));
            Position townHallPos = Position.forUnit(state.getUnit(((DepositGoldAction) action).townHallID));
            Action a = Action.createPrimitiveDeposit(actualID, peasantPosition.getDirection(townHallPos));
            actions.put(actualID, a);
        } else if (action instanceof DepositWoodAction) {
            int actualID = peasantIdMap.get(((DepositWoodAction) action).peasantID);
            Position peasantPosition = Position.forUnit(state.getUnit(actualID));
            Position townHallPos = Position.forUnit(state.getUnit(((DepositWoodAction) action).townHallID));
            Action a = Action.createPrimitiveDeposit(actualID, peasantPosition.getDirection(townHallPos));
            actions.put(actualID, a);
        } else if (action instanceof PickupWoodAction) {
            int actualID = peasantIdMap.get(((PickupWoodAction) action).peasantID);
            Position peasantPosition = Position.forUnit(state.getUnit(actualID));
            Position resourceLocation = Position.forResource(state.getResourceNode(((PickupWoodAction) action).resourceID));
            Action a = Action.createPrimitiveGather(actualID, peasantPosition.getDirection(resourceLocation));
            actions.put(actualID, a);
        } else if (action instanceof PickupGoldAction) {
            int actualID = peasantIdMap.get(((PickupGoldAction) action).peasantID);
            Position peasantPosition = Position.forUnit(state.getUnit(actualID));
            Position resourceLocation = Position.forResource(state.getResourceNode(((PickupGoldAction) action).resourceID));
            Action a = Action.createPrimitiveGather(actualID, peasantPosition.getDirection(resourceLocation));
            actions.put(actualID, a);
        } else if (action instanceof BuildPeasantAction) {
            int townHallID = ((BuildPeasantAction) action).townhallID;
            Action a = Action.createPrimitiveProduction(townHallID, peasantTemplateId);
            actions.put(townHallID, a);
        } else if (action instanceof MultipleAgentStripsAction) {
            MultipleAgentStripsAction actualAction = (MultipleAgentStripsAction) action;
            for (StripsAction a : actualAction.actions) {
                actions.putAll(createSepiaAction(a, state));
            }
        } else {
            System.err.println("Trying to make an action for an undefined StripsAction.\n" + action.toString());
        }
        return actions;
    }

    @Override
    public void terminalStep(State.StateView stateView, History.HistoryView historyView) {

    }

    @Override
    public void savePlayerData(OutputStream outputStream) {

    }

    @Override
    public void loadPlayerData(InputStream inputStream) {

    }
}
