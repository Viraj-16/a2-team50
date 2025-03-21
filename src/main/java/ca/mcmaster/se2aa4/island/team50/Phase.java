package ca.mcmaster.se2aa4.island.team50;

import org.json.JSONObject;

public interface Phase {
    //Called every turn to return the drone's next action, should technicallyy return a valid JSON object such as: { "action": "fly" } or { "action": "scan" }
     
    JSONObject createDecision(Explorer explorer);

    // Called after the drone receives feedback from its last action. Can be used to analyze echo/scan results stored in the Explorer.
     
    void checkDrone(Explorer explorer);

    
     //determines if the phase has finished and then switchs to the next one.
     
    boolean isFinished();

    
     //returns the next phase object once this one is done.
     
    Phase nextPhase();
}
