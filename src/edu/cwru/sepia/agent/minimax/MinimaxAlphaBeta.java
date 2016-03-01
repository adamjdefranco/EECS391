package edu.cwru.sepia.agent.minimax;

import edu.cwru.sepia.action.Action;
import edu.cwru.sepia.agent.Agent;
import edu.cwru.sepia.environment.model.history.History;
import edu.cwru.sepia.environment.model.state.State;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MinimaxAlphaBeta extends Agent {

    private final int numPlys;

    public MinimaxAlphaBeta(int playernum, String[] args)
    {
        super(playernum);

        if(args.length < 1)
        {
            System.err.println("You must specify the number of plys");
            System.exit(1);
        }

        numPlys = Integer.parseInt(args[0]);
    }

    @Override
    public Map<Integer, Action> initialStep(State.StateView newstate, History.HistoryView statehistory) {
        return middleStep(newstate, statehistory);
    }

    @Override
    public Map<Integer, Action> middleStep(State.StateView newstate, History.HistoryView statehistory) {
        GameStateChild bestChild = alphaBetaSearch(new GameStateChild(newstate),
                numPlys,
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY);

        return bestChild.action;
    }

    @Override
    public void terminalStep(State.StateView newstate, History.HistoryView statehistory) {

    }

    @Override
    public void savePlayerData(OutputStream os) {

    }

    @Override
    public void loadPlayerData(InputStream is) {

    }

    /**
     * You will implement this.
     *
     * This is the main entry point to the alpha beta search. Refer to the slides, assignment description
     * and book for more information.
     *
     * Try to keep the logic in this function as abstract as possible (i.e. move as much SEPIA specific
     * code into other functions and methods)
     *
     * @param node The action and state to search from
     * @param depth The remaining number of plys under this node
     * @param alpha The current best value for the maximizing node from this node to the root
     * @param beta The current best value for the minimizing node from this node to the root
     * @return The best child of this node with updated values
     */
    public GameStateChild alphaBetaSearch(GameStateChild node, int depth, double alpha, double beta) {
        double value;
        GameStateChild nodeToReturn;

        //TODO: if node is terminal node
        if (depth == 0 || node.state.allMyUnitsDead() || node.state.allEnemyUnitsDead()) {
            return node;
        }

        //Maximizing
        if (node.state.isMyTurn) {
            value = Double.NEGATIVE_INFINITY;
            // Reverse the ordered list of children for maximizing so the list goes from largest to smallest
            List<GameStateChild> children = Collections.reverse(orderChildrenWithHeuristics(node.state.getChildren());
            nodeToReturn = children.get(0);
            for (GameStateChild child : children) {
                GameStateChild childsBestNode = alphaBetaSearch(child, depth - 1, alpha, beta);
                if(childsBestNode.state.getUtility() > value) {
                    value = childsBestNode.state.getUtility();
                    nodeToReturn = childsBestNode;
                }
                //value = Math.max(value, alphaBetaSearch(child, depth - 1, alpha, beta).state.getUtility());
                alpha = Math.max(value, alpha);
                //Prune Beta
                if (beta <= alpha) {
                    break;
                }
            }
            return nodeToReturn;
            //Minimizing
        } else {
            value = Double.POSITIVE_INFINITY;
            List<GameStateChild> children = orderChildrenWithHeuristics(node.state.getChildren());
            nodeToReturn = children.get(0);
            for (GameStateChild child : children) {
                //value = Math.min(value, alphaBetaSearch(child, depth - 1, alpha, beta));
                GameStateChild childsBestNode = alphaBetaSearch(child, depth - 1, alpha, beta);
                if(childsBestNode.state.getUtility() < value) {
                    value = childsBestNode.state.getUtility();
                    nodeToReturn = childsBestNode;
                }
                alpha = Math.min(value, beta);
                //Prune Alpha
                if (beta <= alpha) {
                    break;
                }
            }
            return nodeToReturn;
        }

        return node;
    }

    /**
     * You will implement this.
     *
     * Given a list of children you will order them according to heuristics you make up.
     * See the assignment description for suggestions on heuristics to use when sorting.
     *
     * Use this function inside of your alphaBetaSearch method.
     *
     * Include a good comment about what your heuristics are and why you chose them.
     *
     * @param children
     * @return The list of children sorted by your heuristic.
     */
    public List<GameStateChild> orderChildrenWithHeuristics(List<GameStateChild> children)
    {
        children.sort((o1, o2) -> Double.compare(o1.state.getUtility(),o2.state.getUtility()));
        return children;
    }
}
