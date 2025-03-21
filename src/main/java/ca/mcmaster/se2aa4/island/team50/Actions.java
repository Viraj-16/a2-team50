package ca.mcmaster.se2aa4.island.team50;

import org.json.JSONObject;

public class Actions {

    // Sends echo command in specified direction
    public void echo(JSONObject parameter, JSONObject decision, Direction direction) {
        parameter.put("direction", direction);
        decision.put("action", "echo");
        decision.put("parameters", parameter);
    }

    // Sends fly command
    public void fly(JSONObject decision) {
        decision.put("action", "fly");
    }

    // Sends stop command
    public void stop(JSONObject decision) {
        decision.put("action", "stop");
    }

    // Sends heading command to change drone's direction
    public void heading(JSONObject parameter, JSONObject decision, Direction direction) {
        parameter.put("direction", direction);
        decision.put("action", "heading");
        decision.put("parameters", parameter);
    }

    // Sends scan command
    public void scan(JSONObject decision) {
        decision.put("action", "scan");
    }
}
