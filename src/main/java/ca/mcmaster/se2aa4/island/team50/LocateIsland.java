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
            int frontRange = explorer.getFrontRange();
            int leftRange = explorer.getLeftRange();
            int rightRange = explorer.getRightRange();

            taskQueue.add(actions.echo(currentDirection));

            if (frontRange > 2) {
                JSONObject scanCmd = new JSONObject();
                actions.scan(scanCmd);
                taskQueue.add(scanCmd);

                JSONObject flyCmd = new JSONObject();
                actions.fly(flyCmd);
                taskQueue.add(flyCmd);

            } else if (frontRange <= 2){
                taskQueue.add(actions.echo(currentDirection.turnRight()));
                taskQueue.add(actions.echo(currentDirection.turnLeft()));
                if (leftRange > rightRange) {
                    turnAndFly(currentDirection.turnLeft(), actions);
                    explorer.directionSetter(currentDirection);
                    turnAndFly(currentDirection.turnLeft(), actions);
                    explorer.directionSetter(currentDirection);
                    taskQueue.add(actions.echo(currentDirection));

                } else {
                    turnAndFly(currentDirection.turnRight(), actions);
                    explorer.directionSetter(currentDirection);
                    turnAndFly(currentDirection.turnRight(), actions);
                    explorer.directionSetter(currentDirection);
                    taskQueue.add(actions.echo(currentDirection));
                }
            }
        }

        return taskQueue.poll();

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
        return islandSpotted; //&& taskQueue.isEmpty();
    }
 
    //@Override
    //public Phase nextPhase() {
      //  return new BeginCoastlineScan(currentDirection); //  gotta make this class next
    //}

    public Direction getCurrentDirection(){
        return currentDirection;
    }

    public JSONObject getCurrentDecision() {
        return currentDecision;
    }

    public Queue<JSONObject> getQueuedTasks() {
        return taskQueue;
    }

    private void turnAndFly(Direction direction, Actions actions) {
        JSONObject headingCmd = new JSONObject();
        JSONObject flyCmd = new JSONObject();
        JSONObject scanCmd = new JSONObject();
        JSONObject param = new JSONObject();

        actions.scan(scanCmd);
        taskQueue.add(scanCmd);

        actions.heading(param, headingCmd, direction);
        taskQueue.add(headingCmd);

        currentDirection = direction;
    }
}

