package ca.mcmaster.se2aa4.island.team50;

import org.json.JSONObject;

public class EmergencySiteSearch implements Phase {

    private boolean finished = false;
    private boolean movingEast = true;
    private int x = 0, y = 0;
    private final int MAX_WIDTH = 20;
    private final int MAX_HEIGHT = 20;

    private DroneController droneController;
    private JSONObject nextMove = null;
    private boolean justScanned = false; 

    public EmergencySiteSearch(Direction startingDirection, int battery) {
        droneController = new DroneController(startingDirection, battery);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public Phase nextPhase() {
        return null;
    }

    @Override
    public JSONObject createDecision(Explorer explorer) {
        if (finished) {
            explorer.getLogger().info("** Search finished — stopping");
            JSONObject stop = new JSONObject();
            stop.put("action", "stop");
            return stop;
        }

        if (!justScanned) {
            justScanned = true;
            explorer.getLogger().info("** Scanning at (" + x + ", " + y + ")");
            return droneController.scan();
        }

        // Use the move calculated from checkDrone()
        justScanned = false;
        if (nextMove != null) {
            JSONObject move = nextMove;
            nextMove = null;
            explorer.getLogger().info("** Moving Decision: " + move.toString());
            return move;
        }

        explorer.getLogger().info("** Warning: no move calculated, defaulting to flyForward()");
        return droneController.flyForward();
    }

    @Override
    public void checkDrone(Explorer explorer) {
        JSONObject extras = explorer.getLastExtras();

        if (extras != null && extras.has("sites")) {
            var sitesArray = extras.getJSONArray("sites");
            for (int i = 0; i < sitesArray.length(); i++) {
                String site = sitesArray.getString(i);
                if (site.toLowerCase().contains("emergency")) {
                    explorer.getLogger().info("** Emergency Site FOUND: " + site);
                    finished = true;
                    return;
                }
            }
        }

        if (explorer.getBatteryLevel() < 300) {
            explorer.getLogger().info("** Battery too low to continue search.");
            finished = true;
            return;
        }

        nextMove = decideNextMove();
}


    private JSONObject decideNextMove() {
        if (movingEast) {
            if (x < MAX_WIDTH - 1) {
                if (droneController.getDirection() != Direction.E) {
                    return droneController.turnTo(Direction.E);
                } else {
                    x++;
                    return droneController.flyForward();
                }
            } else {
                if (y < MAX_HEIGHT - 1) {
                    if (droneController.getDirection() != Direction.S) {
                        return droneController.turnTo(Direction.S);
                    } else {
                        y++;
                        movingEast = false;
                        return droneController.flyForward();
                    }
                } else {
                    finished = true;
                    return droneController.scan();
                }
            }
        } else { // Moving west
            if (x > 0) {
                if (droneController.getDirection() != Direction.W) {
                    return droneController.turnTo(Direction.W);
                } else {
                    x--;
                    return droneController.flyForward();
                }
            } else {
                if (y < MAX_HEIGHT - 1) {
                    if (droneController.getDirection() != Direction.S) {
                        return droneController.turnTo(Direction.S);
                    } else {
                        y++;
                        movingEast = true;
                        return droneController.flyForward();
                    }
                } else {
                    finished = true;
                    return droneController.scan();
                }
            }
        }
    }
}
