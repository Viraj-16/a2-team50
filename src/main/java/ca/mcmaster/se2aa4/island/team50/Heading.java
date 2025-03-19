package ca.mcmaster.se2aa4.island.team50;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Heading {
    
    private String currentHeading;
    private static final Map<String, List<String>> validTurns = new HashMap<>();

    static {
        validTurns.put("NORTH", Arrays.asList("EAST", "WEST"));
        validTurns.put("SOUTH", Arrays.asList("EAST", "WEST"));
        validTurns.put("EAST", Arrays.asList("NORTH", "SOUTH"));
        validTurns.put("WEST", Arrays.asList("NORTH", "SOUTH"));
    }

    public Heading(String initialHeading) {
        this.currentHeading = initialHeading;
    }

    public String getCurrentHeading() {
        String currentHeadingCopy = this.currentHeading;
        return currentHeadingCopy;
    }

    private boolean isValidTurn(String newHeading) {
        return validTurns.get(this.currentHeading).contains(newHeading);
    }

    public void changeHeading(String newHeading) {
        if (isValidTurn(newHeading)) {
            this.currentHeading = newHeading;
        } else {
            throw new IllegalArgumentException("Cannot make immediate U-turn");
        }
    }
}
