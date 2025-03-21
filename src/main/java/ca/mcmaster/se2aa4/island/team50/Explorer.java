package ca.mcmaster.se2aa4.island.team50;

import java.io.StringReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

import ca.mcmaster.se2aa4.island.team50.LocateIsland; //will be implemented
import eu.ace_design.island.bot.IExplorerRaid;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();
    private Direction direction;
    private int battery;

    // stoers the echo results
    private String lastEchoFront = null;
    private String lastEchoLeft = null;
    private String lastEchoRight = null;

    // current phase of the drone will be executing
    private Phase currentPhase;

    @Override
    public void initialize(String s) {
        logger.info("** Initializing the Exploration Command Center");
        JSONObject info = new JSONObject(new JSONTokener(new StringReader(s)));
        direction = Direction.fromString(info.getString("heading"));
        battery = info.getInt("budget");

        // Start with phase 1: locating the island
        currentPhase = new LocateIsland(direction);

        logger.info("Drone starts facing {} with battery {}", direction, battery);
    }

    @Override
    public String takeDecision() {
        if (currentPhase.isFinished()) {
            currentPhase = currentPhase.nextPhase();
        }
        return currentPhase.createDecision(this).toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        int cost = response.getInt("cost");
        battery -= cost;

        JSONObject extras = response.getJSONObject("extras");
        if (extras.has("echo")) {
            JSONObject echo = extras.getJSONObject("echo");
            String found = echo.getString("found");
            String directionStr = echo.getString("direction");

            switch (directionStr) {
                case "FRONT":
                    lastEchoFront = found;
                    break;
                case "LEFT":
                    lastEchoLeft = found;
                    break;
                case "RIGHT":
                    lastEchoRight = found;
                    break;
            }
        }

        currentPhase.checkDrone(this);
    }

    @Override
    public String deliverFinalReport() {
        return "not implemented yet";
    }


    public String getLastEchoFront() {
        return lastEchoFront;
    }

    public String getLastEchoLeft() {
        return lastEchoLeft;
    }

    public String getLastEchoRight() {
        return lastEchoRight;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getBatteryLevel() {
        return battery;
    }
}
