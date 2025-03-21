package ca.mcmaster.se2aa4.island.team50;

import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONObject;

public class LocateIsland implements Phase {

    private final Queue<JSONObject> taskQueue = new LinkedList<>();
    private boolean islandSpotted = false;
    private JSONObject currentDecision;
    private Direction currentDirection;

    public LocateIsland(Direction startDirection) {
        this.currentDirection = startDirection;
    }

    @Override
    public JSONObject createDecision(Explorer explorer) {
        currentDecision = new JSONObject();
        Actions actions = new Actions();
        JSONObject param = new JSONObject();

        if (!taskQueue.isEmpty()) {
            return taskQueue.poll();
        }

        if (!islandSpotted) {
            taskQueue.add(actions.echo(currentDirection));
            taskQueue.add(actions.echo(currentDirection.turnRight()));
            taskQueue.add(actions.echo(currentDirection.turnLeft()));
            return taskQueue.poll();
        }

        JSONObject flyCmd = new JSONObject();
        actions.fly(flyCmd);
        currentDecision = flyCmd;
        return currentDecision;
    }

    @Override
    public void checkDrone(Explorer explorer) {
        String front = explorer.getLastEchoFront();
        String left = explorer.getLastEchoLeft();
        String right = explorer.getLastEchoRight();

        if ("GROUND".equals(front) || "GROUND".equals(left) || "GROUND".equals(right)) {
            islandSpotted = true;
        }
    }

    @Override
    public boolean isFinished() {
        return islandSpotted && taskQueue.isEmpty();
    }
 
    @Override
    public Phase nextPhase() {

        return new BeginCoastlineScan(currentDirection); //  gotta make this class next
    }

    public JSONObject getCurrentDecision() {
        return currentDecision;
    }

    public Queue<JSONObject> getQueuedTasks() {
        return taskQueue;
    }
}

