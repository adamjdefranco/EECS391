package edu.cwru.sepia.agent;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.environment.model.history.DamageLog;
import edu.cwru.sepia.environment.model.history.DeathLog;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.State;
import edu.cwru.sepia.environment.model.state.Unit;

import java.io.*;
import java.util.*;

public class RLAgent extends Agent {

    /**
     * Set in the constructor. Defines how many learning episodes your agent should run for.
     * When starting an episode. If the count is greater than this value print a message
     * and call sys.exit(0)
     */
    public final int numEpisodes;
    /**
     * The current episode that the agent is on. Will be used to terminate the learning phase
     * after the given number of episodes.
     */
    public int currentEpisode;
    /**
     * Convenience boolean for determining if we should be learning or testing the policy
     */
    boolean isTesting;

    /**
     * List of your footmen and your enemies footmen
     */
    private List<Integer> myFootmen;
    private List<Integer> enemyFootmen;

    List<Double> inEpisodeRewards;
    List<Double> cumulativeRewards;
    List<Double> averagedRewards;

    /**
     * Convenience variable specifying enemy agent number. Use this whenever referring
     * to the enemy agent. We will make sure it is set to the proper number when testing your code.
     */
    public static final int ENEMY_PLAYERNUM = 1;

    /**
     * Set this to whatever size your feature vector is.
     */
    public static final int NUM_FEATURES = 5;

    /**
     * Use this random number generator for your epsilon exploration. When you submit we will
     * change this seed so make sure that your agent works for more than the default seed.
     */
    public final Random random = new Random(12345);

    /**
     * Your Q-function weights.
     */
    public Double[] weights;

    /**
     * These variables are set for you according to the assignment definition. You can change them,
     * but it is not recommended. If you do change them please let us know and explain your reasoning for
     * changing them.
     */
    //Discount Factor
    public final double gamma = 0.9;
    public final double learningRate = .0001;
    //Epsilon value for Epsilon-Greedy Exploration Strategy
    public final double epsilon = .02;

    public RLAgent(int playernum, String[] args) {
        super(playernum);

        currentEpisode = 0;

        cumulativeRewards = new ArrayList<>();
        averagedRewards = new ArrayList<>();

        if (args.length >= 1) {
            numEpisodes = Integer.parseInt(args[0]);
            System.out.println("Running " + numEpisodes + " episodes.");
        } else {
            numEpisodes = 10;
            System.out.println("Warning! Number of episodes not specified. Defaulting to 10 episodes.");
        }

        boolean loadWeights = false;
        if (args.length >= 2) {
            loadWeights = Boolean.parseBoolean(args[1]);
        } else {
            System.out.println("Warning! Load weights argument not specified. Defaulting to not loading.");
        }

        if (loadWeights) {
            weights = loadWeights();
        } else {
            // initialize weights to random values between -1 and 1
            weights = new Double[NUM_FEATURES];
            for (int i = 0; i < weights.length; i++) {
                weights[i] = random.nextDouble() * 2 - 1;
            }
        }
    }

    /**
     * We've implemented some setup code for your convenience. Change what you need to.
     */
    @Override
    public Map<Integer, Action> initialStep(State.StateView stateView, History.HistoryView historyView) {
        // Check to see if we are doing learning, or
        // if we are transitioning between learning and tested
        if ((currentEpisode % 15) < 5)
            isTesting = true;
        if (currentEpisode % 15 == 5) {
            isTesting = false;
            cumulativeRewards = new ArrayList<>();
        }
//        currentFeatures = new HashMap<>();

        System.out.println("Episode " + currentEpisode + " -- testing? " + isTesting);

        // Find all of your units
        myFootmen = new LinkedList<>();
        for (Integer unitId : stateView.getUnitIds(playernum)) {
            Unit.UnitView unit = stateView.getUnit(unitId);

            String unitName = unit.getTemplateView().getName().toLowerCase();
            if (unitName.equals("footman")) {
                myFootmen.add(unitId);
            } else {
                System.err.println("Unknown unit type: " + unitName);
            }
        }

        // Find all of the enemy units
        enemyFootmen = new LinkedList<>();
        for (Integer unitId : stateView.getUnitIds(ENEMY_PLAYERNUM)) {
            Unit.UnitView unit = stateView.getUnit(unitId);

            String unitName = unit.getTemplateView().getName().toLowerCase();
            if (unitName.equals("footman")) {
                enemyFootmen.add(unitId);
            } else {
                System.err.println("Unknown unit type: " + unitName);
            }
        }

        return middleStep(stateView, historyView);
    }

    /**
     * You will need to calculate the reward at each step and update your totals. You will also need to
     * check if an event has occurred. If it has then you will need to update your weights and select a new action.
     * <p>
     * If you are using the footmen vectors you will also need to remove killed units. To do so use the historyView
     * to get a DeathLog. Each DeathLog tells you which player's unit died and the unit ID of the dead unit. To get
     * the deaths from the last turn do something similar to the following snippet. Please be aware that on the first
     * turn you should not call this as you will get nothing back.
     * <p>
     * for(DeathLog deathLog : historyView.getDeathLogs(stateView.getTurnNumber() -1)) {
     * System.out.println("Player: " + deathLog.getController() + " unit: " + deathLog.getDeadUnitID());
     * }
     * <p>
     * You should also check for completed actions using the history view. Obviously you never want a footman just
     * sitting around doing nothing (the enemy certainly isn't going to stop attacking). So at the minimum you will
     * have an even whenever one your footmen's targets is killed or an action fails. Actions may fail if the target
     * is surrounded or the unit cannot find a path to the unit. To get the action results from the previous turn
     * you can do something similar to the following. Please be aware that on the first turn you should not call this
     * <p>
     * Map<Integer, ActionResult> actionResults = historyView.getCommandFeedback(playernum, stateView.getTurnNumber() - 1);
     * for(ActionResult result : actionResults.values()) {
     * System.out.println(result.toString());
     * }
     *
     * @return New actions to execute or nothing if an event has not occurred.
     */
    @Override
    public Map<Integer, Action> middleStep(State.StateView stateView, History.HistoryView historyView) {
        return null;

    }

    /**
     * Here you will calculate the cumulative average rewards for your testing episodes. If you have just
     * finished a set of test episodes you will call out testEpisode.
     * <p>
     * It is also a good idea to save your weights with the saveWeights function.
     */
    @Override
    public void terminalStep(State.StateView stateView, History.HistoryView historyView) {

        //Compute
        if (isTesting) {
            double sum = 0.0;
            for (double reward : inEpisodeRewards) {
                sum += reward;
            }
            cumulativeRewards.add(sum / inEpisodeRewards.size());
            if (currentEpisode % 15 == 4) {
                double cumulativeSum = 0.0;
                for (double cumulativeReward : cumulativeRewards) {
                    cumulativeSum += cumulativeReward;
                }
                averagedRewards.add(cumulativeSum / cumulativeRewards.size());
            }
            //If we have reached the end of a test episode, print this data.
            printTestData(averagedRewards);
        }

        //Store the feature values for reference in later episodes.
        for (int friendlyUnit : myFootmen) {
//            previousFeatures.put(friendlyUnit,currentFeatures.get(friendlyUnit));
        }

        currentEpisode++;

        // Save your weights
        saveWeights(weights);

    }

    /**
     * Calculate the updated weights for this agent.
     *
     * @param oldWeights  Weights prior to update
     * @param oldFeatures Features from (s,a)
     * @param totalReward Cumulative discounted reward for this footman.
     * @param stateView   Current state of the game.
     * @param historyView History of the game up until this point
     * @param footmanId   The footman we are updating the weights for
     * @return The updated weight vector.
     */
    public double[] updateWeights(double[] oldWeights, double[] oldFeatures, double totalReward, State.StateView stateView, History.HistoryView historyView, int footmanId) {
        if (isTesting) {
            return oldWeights;
        }
        double prevQ = 0.0;
        for (int i = 0; i < oldFeatures.length; i++) {
            prevQ += oldFeatures[i] * oldWeights[i];
        }

        int bestDefenderID = selectAction(stateView,historyView,footmanId);
        double qNew = calcQValue(stateView,historyView,footmanId,bestDefenderID);
        for (int i = 0; i < oldWeights.length; i++) {
            oldWeights[i] = oldWeights[i] + learningRate * (totalReward + gamma * qNew - prevQ) * oldFeatures[i];
        }
        return oldWeights;
    }

    /**
     * Given a footman and the current state and history of the game select the enemy that this unit should
     * attack. This is where you would do the epsilon-greedy action selection.
     *
     * @param stateView   Current state of the game
     * @param historyView The entire history of this episode
     * @param attackerId  The footman that will be attacking
     * @return The enemy footman ID this unit should attack
     */
    public int selectAction(State.StateView stateView, History.HistoryView historyView, int attackerId) {
        int enemyID = -1;
        // Decide whether or not to follow the policy based on the Epsilon Greedy Exploration Strategy
        Double percentToFollowPolicy = random.nextDouble();
        if (percentToFollowPolicy < epsilon) {
            //We aren't following the policy anymore, instead pick a random enemy to attack.
            int randomEnemyIndex = random.nextInt(enemyFootmen.size());
            enemyID = enemyFootmen.get(randomEnemyIndex);
        } else {
            // We are following the policy. Look at the Q value for attacking each enemy and
            // pick the enemy whose q value is largest.
            double maxQValue = Double.NEGATIVE_INFINITY;
            for (int enemy : enemyFootmen) {
                double qValue = calcQValue(stateView, historyView, attackerId, enemy);
                if (qValue > maxQValue) {
                    maxQValue = qValue;
                    enemyID = enemy;
                }
            }
        }
        return enemyID;
    }

    /**
     * Given the current state and the footman in question calculate the reward received on the last turn.
     * This is where you will check for things like Did this footman take or give damage? Did this footman die
     * or kill its enemy. Did this footman start an action on the last turn? See the assignment description
     * for the full list of rewards.
     * <p>
     * Remember that you will need to discount this reward based on the timestep it is received on. See
     * the assignment description for more details.
     * <p>
     * As part of the reward you will need to calculate if any of the units have taken damage. You can use
     * the history view to get a list of damages dealt in the previous turn. Use something like the following.
     * <p>
     * for(DamageLog damageLogs : historyView.getDamageLogs(lastTurnNumber)) {
     * System.out.println("Defending player: " + damageLog.getDefenderController() + " defending unit: " + \
     * damageLog.getDefenderID() + " attacking player: " + damageLog.getAttackerController() + \
     * "attacking unit: " + damageLog.getAttackerID());
     * }
     * <p>
     * You will do something similar for the deaths. See the middle step documentation for a snippet
     * showing how to use the deathLogs.
     * <p>
     * To see if a command was issued you can check the commands issued log.
     * <p>
     * Map<Integer, Action> commandsIssued = historyView.getCommandsIssued(playernum, lastTurnNumber);
     * for (Map.Entry<Integer, Action> commandEntry : commandsIssued.entrySet()) {
     * System.out.println("Unit " + commandEntry.getKey() + " was command to " + commandEntry.getValue().toString);
     * }
     *
     * @param stateView   The current state of the game.
     * @param historyView History of the episode up until this turn.
     * @param footmanId   The footman ID you are looking for the reward from.
     * @return The current reward
     */
    public double calculateReward(State.StateView stateView, History.HistoryView historyView, int footmanId) {
        double reward = 0;
        int turn = stateView.getTurnNumber() - 1;

        // Loops through the last five turns
        for (int i = 0; i < 5; i++) {
            double rewardForTurn = 0.0;

            // Calculates rewards for damage
            for (DamageLog dlog : historyView.getDamageLogs(turn)) {
                if (myFootmen.contains(dlog.getDefenderID())) {
                    rewardForTurn -= dlog.getDamage();
                } else {
                    rewardForTurn += dlog.getDamage();
                }
            }

            // Calculates rewards for deaths
            for (DeathLog dlog : historyView.getDeathLogs(turn)) {
                if (dlog.getController() == ENEMY_PLAYERNUM) {
                    rewardForTurn += 100;
                } else {
                    rewardForTurn -= 100;
                }
            }

            // Calculates rewards for previous actions
            rewardForTurn -= historyView.getCommandsIssued(playernum, turn).get(footmanId) != null?0.1:0;

            // Adds the reward for a single turn (with respect to gamma) into the reward for the set of 5 turns
            reward += rewardForTurn * gamma;

            // Prepare to look at the previous turn
            turn--;
        }

        return reward;
    }

    /**
     * Calculate the Q-Value for a given state action pair. The state in this scenario is the current
     * state view and the history of this episode. The action is the attacker and the enemy pair for the
     * SEPIA attack action.
     * <p>
     * This returns the Q-value according to your feature approximation. This is where you will calculate
     * your features and multiply them by your current weights to get the approximate Q-value.
     *
     * @param stateView   Current SEPIA state
     * @param historyView Episode history up to this point in the game
     * @param attackerId  Your footman. The one doing the attacking.
     * @param defenderId  An enemy footman that your footman would be attacking
     * @return The approximate Q-value
     */
    public double calcQValue(State.StateView stateView, History.HistoryView historyView, int attackerId, int defenderId) {
        double[] featureVector = calculateFeatureVector(stateView, historyView, attackerId, defenderId);
        double qVal = 0;

        // Calculates cumulative some of the features times their respective weights
        for (int i = 0; i < featureVector.length; i++) {
            qVal += featureVector[i] * weights[i];
        }

        return qVal;
    }

    /**
     * Given a state and action calculate your features here. Please include a comment explaining what features
     * you chose and why you chose them.
     * <p>
     * All of your feature functions should evaluate to a double. Collect all of these into an array. You will
     * take a dot product of this array with the weights array to get a Q-value for a given state action.
     * <p>
     * It is a good idea to make the first value in your array a constant. This just helps remove any offset
     * from 0 in the Q-function. The other features are up to you. Many are suggested in the assignment
     * description.
     *
     * @param stateView   Current state of the SEPIA game
     * @param historyView History of the game up until this turn
     * @param attackerId  Your footman. The one doing the attacking.
     * @param defenderId  An enemy footman. The one you are considering attacking.
     * @return The array of feature function outputs.
     */
    public double[] calculateFeatureVector(State.StateView stateView, History.HistoryView historyView, int attackerId, int defenderId) {
        return null;
    }

    /**
     * DO NOT CHANGE THIS!
     * <p>
     * Prints the learning rate data described in the assignment. Do not modify this method.
     *
     * @param averageRewards List of cumulative average rewards from test episodes.
     */
    public void printTestData(List<Double> averageRewards) {
        System.out.println("");
        System.out.println("Games Played      Average Cumulative Reward");
        System.out.println("-------------     -------------------------");
        for (int i = 0; i < averageRewards.size(); i++) {
            String gamesPlayed = Integer.toString(10 * i);
            String averageReward = String.format("%.2f", averageRewards.get(i));

            int numSpaces = "-------------     ".length() - gamesPlayed.length();
            StringBuffer spaceBuffer = new StringBuffer(numSpaces);
            for (int j = 0; j < numSpaces; j++) {
                spaceBuffer.append(" ");
            }
            System.out.println(gamesPlayed + spaceBuffer.toString() + averageReward);
        }
        System.out.println("");
    }

    /**
     * DO NOT CHANGE THIS!
     * <p>
     * This function will take your set of weights and save them to a file. Overwriting whatever file is
     * currently there. You will use this when training your agents. You will include th output of this function
     * from your trained agent with your submission.
     * <p>
     * Look in the agent_weights folder for the output.
     *
     * @param weights Array of weights
     */
    public void saveWeights(Double[] weights) {
        File path = new File("agent_weights/weights.txt");
        // create the directories if they do not already exist
        path.getAbsoluteFile().getParentFile().mkdirs();

        try {
            // open a new file writer. Set append to false
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, false));

            for (double weight : weights) {
                writer.write(String.format("%f\n", weight));
            }
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            System.err.println("Failed to write weights to file. Reason: " + ex.getMessage());
        }
    }

    /**
     * DO NOT CHANGE THIS!
     * <p>
     * This function will load the weights stored at agent_weights/weights.txt. The contents of this file
     * can be created using the saveWeights function. You will use this function if the load weights argument
     * of the agent is set to 1.
     *
     * @return The array of weights
     */
    public Double[] loadWeights() {
        File path = new File("agent_weights/weights.txt");
        if (!path.exists()) {
            System.err.println("Failed to load weights. File does not exist");
            return null;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line;
            List<Double> weights = new LinkedList<>();
            while ((line = reader.readLine()) != null) {
                weights.add(Double.parseDouble(line));
            }
            reader.close();

            return weights.toArray(new Double[weights.size()]);
        } catch (IOException ex) {
            System.err.println("Failed to load weights from file. Reason: " + ex.getMessage());
        }
        return null;
    }

    @Override
    public void savePlayerData(OutputStream outputStream) {

    }

    @Override
    public void loadPlayerData(InputStream inputStream) {

    }
}
