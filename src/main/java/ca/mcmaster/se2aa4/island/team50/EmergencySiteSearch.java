package ca.mcmaster.se2aa4.island.team50;

import org.json.JSONObject;

public class EmergencySiteSearch implements Phase {

    private boolean finished = false;
    private boolean movingEast = true;
    private int x = 0, y = 0; // Start at grid position
    private final int MAX_WIDTH = 20;  // Adjust based on map size
    private final int MAX_HEIGHT = 20;

    private DroneController droneController;

    public EmergencySiteSearch(Direction startingDirection, int battery) {
        droneController = new DroneController(startingDirection, battery);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public Phase nextPhase() {
        return null; // No further phase
    }

    @Override
    public JSONObject createDecision(Explorer explorer) {
        // 1. Always scan first
        return droneController.scan();
    }

    @Override
    public void checkDrone(Explorer explorer) {
        JSONObject extras = explorer.getLastExtras();

        // Check for emergency site in scan result
        if (extras != null && extras.has("POI")) {
            String poi = extras.getString("POI");
            if (poi.contains("Emergency")) {
                System.out.println("Emergency Site Found at: " + poi);
                finished = true;
                return;
            }
        }

        // Battery safety
        if (explorer.getBatteryLevel() < 300) {
            System.out.println("Battery too low. Ending search.");
            finished = true;
            return;
        }

        // After scan, move next
        JSONObject moveDecision = decideNextMove(explorer);
    }

    private JSONObject decideNextMove(Explorer explorer) {
        // Directional movement logic (left-right grid search)

        if (movingEast) {
            if (x < MAX_WIDTH - 1) {
                if (droneController.getDirection() != Direction.E) {
                    return droneController.turnTo(Direction.E);
                } else {
                    x++;
                    return droneController.flyForward();
                }
            } else {
                if (droneController.getDirection() != Direction.S) {
                    return droneController.turnTo(Direction.S);
                } else {
                    y++;
                    movingEast = false;
                    return droneController.flyForward();
                }
            }
        } else { // Moving West
            if (x > 0) {
                if (droneController.getDirection() != Direction.W) {
                    return droneController.turnTo(Direction.W);
                } else {
                    x--;
                    return droneController.flyForward();
                }
            } else {
                if (droneController.getDirection() != Direction.S) {
                    return droneController.turnTo(Direction.S);
                } else {
                    y++;
                    movingEast = true;
                    return droneController.flyForward();
                }
            }
        }
    }
}
