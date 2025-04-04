package ca.mcmaster.se2aa4.island.team50;

import java.io.StringReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;

import ca.mcmaster.se2aa4.island.team50.LocateIsland;
import ca.mcmaster.se2aa4.island.team50.Phase;
import java.util.HashSet;
import java.util.Set;
import eu.ace_design.island.bot.IExplorerRaid;
import java.time.chrono.ThaiBuddhistChronology;

public class Explorer implements IExplorerRaid {

    private final Logger logger = LogManager.getLogger();
    private Direction direction;
    private int battery;
    private Set<String> creeks = new HashSet<>();

    // Stores echo results
    private String lastEchoFront = null;
    private String lastEchoLeft = null;
    private String lastEchoRight = null;

    private int frontRange = 3;
    private int leftRange = -1;
    private int rightRange = -1;

    // Stores scan/echo extras
    private JSONObject lastExtras = null;

    // Stores last decision
    private JSONObject lastDecision = null;

    // Stores last direction echoed in order to assign found and range values
    private String lastDirection = null;

    // Current phase of the drone will be executing
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
        currentPhase.checkDrone(this);
        if (currentPhase.isFinished()) {
            currentPhase = currentPhase.nextPhase();
            
            
        }

        if (currentPhase == null){
            JSONObject decision = new JSONObject();
            decision.put("action", "stop");
            logger.info("** Decision: {}",decision.toString());
            return decision.toString();
        }
        
        lastDecision = currentPhase.createDecision(this);
        return lastDecision.toString();
    }

    @Override
    public void acknowledgeResults(String s) {
        JSONObject response = new JSONObject(new JSONTokener(new StringReader(s)));
        int cost = response.getInt("cost");
        battery -= cost;

        if (lastDecision.has("parameters")){
            JSONObject parameter = lastDecision.getJSONObject("parameters");
            lastDirection = parameter.getString("direction");
            logger.info(lastDirection);
        }
        

        if (response.has("extras")) {
            lastExtras = response.getJSONObject("extras");

            if (lastExtras.has("found")){
                String found = lastExtras.getString("found");
                int range = lastExtras.getInt("range");
                if (lastDirection.equals(direction.toString())){
                    lastEchoFront = found;
                    frontRange = range;
                } else if (lastDirection.equals(direction.turnLeft().toString())){
                    lastEchoLeft = found;
                    leftRange = range;
                } else if (lastDirection.equals(direction.turnRight().toString())){
                    lastEchoRight = found;
                    rightRange = range;
                }

                logger.info(found);
                logger.info(range);

                if (lastDirection.equals(direction.toString())){
                    lastEchoFront = found;
                    frontRange = range;
                } else if (lastDirection.equals(direction.turnLeft().toString())){
                    lastEchoLeft = found;
                    leftRange = range;
                } else if (lastDirection.equals(direction.turnRight().toString())){
                    lastEchoRight = found;
                    rightRange = range;
                }

            } else if (lastExtras.has("creeks")) {
                for (Object creek : lastExtras.getJSONArray("creeks")) {
                    creeks.add(creek.toString());
                }
            } 
        }
        logger.info("Updated ranges: Front = " + frontRange+ ", Left = " + leftRange+ ", Right = " + rightRange);
        logger.info("Updated echoes "+ lastEchoLeft+" "+lastEchoFront+" "+lastEchoRight);

        if (currentPhase != null){
            currentPhase.checkDrone(this);
        }
        }

    @Override
    public String deliverFinalReport() {
        return "not implemented yet";
    }

    public void directionSetter(Direction newDirection){
        this.direction = newDirection;
    }

    // Getters
    public String getLastEchoFront() {
        return lastEchoFront;
    }

    public String getLastEchoLeft() {
        return lastEchoLeft;
    }

    public String getLastEchoRight() {
        return lastEchoRight;
    }

    public int getFrontRange() {
        return frontRange;
    }

    public int getLeftRange() {
        return leftRange;
    }

    public int getRightRange() {
        return rightRange;
    }

    public JSONObject getLastExtras() {
        return lastExtras;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getBatteryLevel() {
        return battery;
    }

    public Set<String> getCreeks() {
        return creeks;
    }

    public Logger getLogger() {
        return logger;
    }
    
}
