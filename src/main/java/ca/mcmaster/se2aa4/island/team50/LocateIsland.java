package ca.mcmaster.se2aa4.island.team50;

import java.util.LinkedList;
import java.util.Queue;

import org.json.JSONObject;

public class LocateIsland implements Phase {

    private final Queue<JSONObject> taskQueue = new LinkedList<>();
    private boolean islandSpotted = false;
    private JSONObject currentDecision;
    private Direction currentDirection;
    //private int frontRange, leftRange, rightRange;

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
            //return taskQueue.poll();

            if (explorer.getFrontRange() > 0) {
                JSONObject flyCmd = new JSONObject();
                actions.fly(flyCmd);
                taskQueue.add(flyCmd);
                JSONObject scanCmd = new JSONObject();
                actions.scan(scanCmd);
                taskQueue.add(scanCmd);

            } else {
                if (explorer.getLeftRange() > explorer.getRightRange()) {
                    taskQueue.add(turnAndFly(currentDirection.turnLeft(), actions));
                    taskQueue.add(turnAndFly(currentDirection.turnLeft(), actions));
                    //currentDirection = currentDirection.turnLeft();

                } else {
                    taskQueue.add(turnAndFly(currentDirection.turnRight(), actions));
                    taskQueue.add(turnAndFly(currentDirection.turnRight(), actions));
                    //currentDirection = currentDirection.turnRight();
                }
            }
        }

        return taskQueue.poll();

        /**JSONObject flyCmd = new JSONObject();
        actions.fly(flyCmd);
        currentDecision = flyCmd;
        return currentDecision;**/
    }

    @Override
    public void checkDrone(Explorer explorer) {
        String front = explorer.getLastEchoFront();
        String left = explorer.getLastEchoLeft();
        String right = explorer.getLastEchoRight();
        int frontRange = explorer.getFrontRange();
        int leftRange = explorer.getLeftRange();
        int rightRange = explorer.getRightRange();

        if (("GROUND".equals(front) && frontRange == 0) || ("GROUND".equals(left) && leftRange == 0) || ("GROUND".equals(right) && rightRange == 0)) {
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

    private JSONObject turnAndFly(Direction direction, Actions actions) {
        JSONObject decision = new JSONObject();
        JSONObject param = new JSONObject();

        actions.heading(param, decision, direction);
        actions.fly(decision);
        actions.scan(decision);
        //actions.heading(param, decision, direction);
        currentDirection = direction;
        return decision;
    }
}

