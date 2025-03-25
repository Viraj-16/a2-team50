package ca.mcmaster.se2aa4.island.team50;

import org.json.JSONObject;
import java.util.LinkedList;
import java.util.Queue;

public class EmergencySiteSearch implements Phase {

    private boolean finished = false;
    private boolean movingEast = true;
    private int x = 0, y = 0;
    private final int MAX_WIDTH = 20;
    private final int MAX_HEIGHT = 20;

    private final Queue<JSONObject> taskQueue = new LinkedList<>();
    private Direction currentDirection;
    private JSONObject currentDecision;

    private DroneController droneController;
    private Actions actions;

    public EmergencySiteSearch(Direction startingDirection, int battery) {
        this.currentDirection = startingDirection;
        this.droneController = new DroneController(startingDirection, battery);
        this.actions = new Actions();
    }

    @Override
    public JSONObject createDecision(Explorer explorer) {
        currentDecision = new JSONObject();

        if (!taskQueue.isEmpty()) {
            return taskQueue.poll();
        }

        // Always scan first if no pending tasks
        JSONObject scanCmd = new JSONObject();
        actions.scan(scanCmd);
        taskQueue.add(scanCmd);

        return taskQueue.poll();
    }

    @Override
    public void checkDrone(Explorer explorer) {
        JSONObject extras = explorer.getLastExtras();

        // Check for emergency site
        if (extras != null && extras.has("POI")) {
            String poi = extras.getString("POI");
            if (poi.contains("Emergency")) {
                explorer.getLogger().info("** Emergency Site Found: " + poi);
                finished = true;
                return;
            }
        }

        // Battery check
        if (explorer.getBatteryLevel() < 300) {
            explorer.getLogger().info("** Battery low. Ending search.");
            finished = true;
            return;
        }

        // Plan next movement
        planNextMove();
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    private void planNextMove() {
        if (movingEast) {
            if (x < MAX_WIDTH - 1) {
                if (currentDirection != Direction.E) {
                    addTurnCommand(Direction.E);
                }
                addFlyCommand();
                x++;
            } else {
                if (y < MAX_HEIGHT - 1) {
                    if (currentDirection != Direction.S) {
                        addTurnCommand(Direction.S);
                    }
                    addFlyCommand();
                    y++;
                    movingEast = false;
                } else {
                    finished = true;
                    JSONObject finalScan = new JSONObject();
                    actions.scan(finalScan);
                    taskQueue.add(finalScan);
                }
            }
        } else {
            if (x > 0) {
                if (currentDirection != Direction.W) {
                    addTurnCommand(Direction.W);
                }
                addFlyCommand();
                x--;
            } else {
                if (y < MAX_HEIGHT - 1) {
                    if (currentDirection != Direction.S) {
                        addTurnCommand(Direction.S);
                    }
                    addFlyCommand();
                    y++;
                    movingEast = true;
                } else {
                    finished = true;
                    JSONObject finalScan = new JSONObject();
                    actions.scan(finalScan);
                    taskQueue.add(finalScan);
                }
            }
        }
    }

    private void addTurnCommand(Direction newDir) {
        JSONObject headingCmd = new JSONObject();
        JSONObject param = new JSONObject();
        actions.heading(param, headingCmd, newDir);
        currentDirection = newDir;
        taskQueue.add(headingCmd);
    }

    private void addFlyCommand() {
        JSONObject flyCmd = new JSONObject();
        actions.fly(flyCmd);
        taskQueue.add(flyCmd);
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public JSONObject getCurrentDecision() {
        return currentDecision;
    }

    public Queue<JSONObject> getQueuedTasks() {
        return taskQueue;
    }
}
