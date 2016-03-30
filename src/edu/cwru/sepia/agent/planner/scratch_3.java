package edu.cwru.sepia.agent.planner;

import edu.cwru.sepia.environment.model.state.ResourceNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class scratch_3 {
    public static void main(String[] args) {

        Map<Integer, Resource> resourceMap = new HashMap<>();
        resourceMap.put(1,new Resource(1,new Position(1,5),100, ResourceNode.Type.GOLD_MINE));
        resourceMap.put(2,new Resource(2,new Position(1,7),100, ResourceNode.Type.TREE));
        resourceMap.put(7,new Resource(7,new Position(2,6),200, ResourceNode.Type.TREE));

        TownHall hall = new TownHall(3, new Position(0,0), 100, 100, false);

        Map<Integer, Peasant> peasantMap = new HashMap<>();
        peasantMap.put(4, new Peasant(4, new Position(2,4)));
        peasantMap.put(80, new Peasant(5, new Position(3,4)));
        peasantMap.put(6, new Peasant(6, new Position(3,5)));

        GameState initialState = new GameState(resourceMap, peasantMap, hall,new ArrayList<>(),0);
        List<GameState> children = initialState.generateChildren();
        children.forEach(child->{
            System.out.println("Child Start");
            child.peasants.values().forEach(p->{
                System.out.println("ID: "+p.id+", Next to Wood: "+p.isAdjacentWoodSource()+", Next to Gold: "+p.isAdjacentGoldSource());
            });
            System.out.println("Child End");
        });
        System.out.println(children.size());

    }
}