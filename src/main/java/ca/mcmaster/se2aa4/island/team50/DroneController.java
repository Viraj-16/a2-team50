package ca.mcmaster.se2aa4.island.team50;

import org.json.JSONObject;

public class DroneController {

    private Direction direction;
    private int battery;

    public DroneController(Direction startDirection, int startBattery) {
        this.direction = startDirection;
        this.battery = startBattery;
    }

    // Turn to desired direction
    public JSONObject turnTo(Direction desiredDirection) {
        JSONObject decision = new JSONObject();
        decision.put("action", "heading");
        decision.put("parameters", new JSONObject().put("direction", desiredDirection.toString()));
        this.direction = desiredDirection;
        return decision;
    }

    // Move forward
    public JSONObject flyForward() {
        JSONObject decision = new JSONObject();
        decision.put("action", "fly");
        return decision;
    }

    // Scan current tile
    public JSONObject scan() {
        JSONObject decision = new JSONObject();
        decision.put("action", "scan");
        return decision;
    }

    // Update battery after each move
    public void updateBattery(int cost) {
        battery -= cost;
    }

    public int getBatteryLevel() {
        return battery;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction d) {
        direction = d;
    }
}
