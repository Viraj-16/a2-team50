package ca.mcmaster.se2aa4.island.team50;

import org.json.JSONObject;

public class EmergencySiteSearch implements Phase {

    private boolean finished = false;
    private boolean movingEast = true;
    private int x = 0, y = 0;
    private final int MAX_WIDTH = 20;
    private final int MAX_HEIGHT = 20;

    private DroneController droneController;
    private JSONObject nextMove = null;  // Store next move decision

    public EmergencySiteSearch(Direction startingDirection, int battery) {
        droneController = new DroneController(startingDirection, battery);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    //@Override
    //public Phase nextPhase() {
    //    return null;
    //}

    @Override
    public JSONObject createDecision(Explorer explorer) {
        // Prioritize any stored movement decision first
        if (nextMove != null) {
            JSONObject move = nextMove;
            nextMove = null;  // Clear after using
            explorer.getLogger().info("** Moving Decision: " + move.toString());
            return move;
        }

        // Always scan first
        JSONObject scanDecision = droneController.scan();
        explorer.getLogger().info("** Scanning at (" + x + ", " + y + ")");
        return scanDecision;
    }

    @Override
    public void checkDrone(Explorer explorer) {
        JSONObject extras = explorer.getLastExtras();

        // Check if emergency site detected
        if (extras != null && extras.has("POI")) {
            String poi = extras.getString("POI");
            if (poi.contains("Emergency")) {
                explorer.getLogger().info("** Emergency Site Found: " + poi);
                finished = true;
                return;
            }
        }

        // Battery safety
        if (explorer.getBatteryLevel() < 300) {
            explorer.getLogger().info("** Battery low. Ending search.");
            finished = true;
            return;
        }

        // Plan next move (save it, used in createDecision)
        nextMove = decideNextMove(explorer);
    }

    private JSONObject decideNextMove(Explorer explorer) {
        if (movingEast) {
            if (x < MAX_WIDTH - 1) {
                if (droneController.getDirection() != Direction.E) {
                    return droneController.turnTo(Direction.E);
                } else {
                    x++;
                    return droneController.flyForward();
                }
            } else {
                // End of row
                if (y < MAX_HEIGHT - 1) {
                    if (droneController.getDirection() != Direction.S) {
                        return droneController.turnTo(Direction.S);
                    } else {
                        y++;
                        movingEast = false;
                        return droneController.flyForward();
                    }
                } else {
                    // Reached bottom
                    finished = true;
                    return droneController.scan();  // Final scan
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
                // End of row
                if (y < MAX_HEIGHT - 1) {
                    if (droneController.getDirection() != Direction.S) {
                        return droneController.turnTo(Direction.S);
                    } else {
                        y++;
                        movingEast = true;
                        return droneController.flyForward();
                    }
                } else {
                    // Reached bottom
                    finished = true;
                    return droneController.scan();  // Final scan
                }
            }
        }
    }
}
