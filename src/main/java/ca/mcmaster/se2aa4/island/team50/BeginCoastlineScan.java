package ca.mcmaster.se2aa4.island.team50;

import ca.mcmaster.se2aa4.island.team50.Actions;
import ca.mcmaster.se2aa4.island.team50.Direction;
import ca.mcmaster.se2aa4.island.team50.Explorer;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Queue;
import java.util.HashSet;
import java.util.Set;

public class BeginCoastlineScan implements Phase {

    private final Queue<JSONObject> taskQueue = new LinkedList<>();
    private final Set<String> creeks = new HashSet<>();
    private Direction direction;
    private int flightCounter = 0;
    private final int maxFlightCycles = 80; // Atemporary limit to avoid infinite loop

    public BeginCoastlineScan(Direction direction) {
        this.direction = direction;
    }

    @Override
    public JSONObject createDecision(Explorer explorer) {
        Actions actions = new Actions();
        JSONObject decision = new JSONObject();

        if (!taskQueue.isEmpty()) {
            return taskQueue.poll();
        }

        // Simplified right-hand rule: scan every 3 steps
        if (flightCounter % 3 == 0) {
            actions.scan(decision);
        } else {
            actions.fly(decision);
        }

        flightCounter++;
        return decision;
    }

    @Override
    public void checkDrone(Explorer explorer) {
        JSONObject extras = explorer.getLastExtras();

        if (extras != null && extras.has("creeks")) {
            for (Object creek : extras.getJSONArray("creeks")) {
                creeks.add(creek.toString());
            }
        }
    }

    @Override
    public boolean isFinished() {
        return flightCounter >= maxFlightCycles;
    }

    @Override
    public Phase nextPhase() {
        return null; // Replace with next  phase later
    }

    public Set<String> getFoundCreeks() {
        return creeks;
    }
}
