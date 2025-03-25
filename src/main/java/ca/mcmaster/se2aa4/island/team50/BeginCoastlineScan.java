package ca.mcmaster.se2aa4.island.team50;

import ca.mcmaster.se2aa4.island.team50.Actions;
import ca.mcmaster.se2aa4.island.team50.Direction;
import ca.mcmaster.se2aa4.island.team50.Explorer;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Queue;
import java.util.HashSet;
import java.util.Set;

public class BeginCoastlineScan implements Phase {

    private final Queue<JSONObject> taskQueue = new LinkedList<>();
    private final Set<String> creeks = new HashSet<>();
    private Direction direction;
    private int flightCounter = 0;

    public BeginCoastlineScan(Direction newDirection) {
        this.direction = newDirection;
    }

    @Override
    public JSONObject createDecision(Explorer explorer) {
        Actions actions = new Actions();
        
        JSONObject flyCmd = new JSONObject();
        JSONObject scanCmd = new JSONObject();

        if (!taskQueue.isEmpty()) {
            return taskQueue.poll();
        }
        
        if (flightCounter == 0){
            actions.scan(scanCmd);
            taskQueue.add(scanCmd);

            actions.fly(flyCmd);
            taskQueue.add(flyCmd);

            actions.scan(scanCmd);
            taskQueue.add(scanCmd);

            actions.fly(flyCmd);
            taskQueue.add(flyCmd);

        } else {
            if (creeks.isEmpty()){
                taskQueue.add(actions.echo(direction));

                if (explorer.getLastEchoFront().equals("GROUND")) {
                    actions.scan(scanCmd);
                    taskQueue.add(scanCmd);

                    actions.fly(flyCmd);
                    taskQueue.add(flyCmd);

                } else if (explorer.getLastEchoFront().equals("OUT_OF_RANGE")){
                    if (direction.toString().equals("W")) {
                        turnAndFly(direction.turnLeft(), actions);
                        explorer.directionSetter(direction);
                        turnAndFly(direction.turnLeft(), actions);
                        explorer.directionSetter(direction);
                        taskQueue.add(actions.echo(direction));

                    } else if (direction.toString().equals("E")){
                        turnAndFly(direction.turnRight(), actions);
                        explorer.directionSetter(direction);
                        turnAndFly(direction.turnRight(), actions);
                        explorer.directionSetter(direction);
                        taskQueue.add(actions.echo(direction));
                    }
                }
            }
        }

        flightCounter++;
        return taskQueue.poll();
    }

    @Override
    public void checkDrone(Explorer explorer) {
        JSONObject extras = explorer.getLastExtras();

        if (extras.has("creeks")) {
            for (Object creek : extras.getJSONArray("creeks")) {
                creeks.add(creek.toString());
            }
        }
    }

    @Override
    public boolean isFinished() {
        return !creeks.isEmpty();
    }

    @Override
    public Phase nextPhase() {
        return null; // Replace with next  phase later (EmergencySiteSearch)  return new EmergencySiteSearch(currentDirection, batteryLevel);
    }

    public Set<String> getFoundCreeks() {
        return creeks;
    }

    private void turnAndFly(Direction newDirection, Actions actions) {
        JSONObject headingCmd = new JSONObject();
        JSONObject flyCmd = new JSONObject();
        JSONObject scanCmd = new JSONObject();
        JSONObject param = new JSONObject();

        actions.scan(scanCmd);
        taskQueue.add(scanCmd);

        actions.heading(param, headingCmd, newDirection);
        taskQueue.add(headingCmd);

        direction = newDirection;
    }
}
