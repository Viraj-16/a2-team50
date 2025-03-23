package ca.mcmaster.se2aa4.island.team50;

public enum Direction {
    N,
    E,
    S,
    W;

    // Converts string to Direction (e.g., "N" -> Direction.N)
    public static Direction fromString(String dir) {
        switch (dir.toUpperCase()) {
            case "N":
                return N;
            case "E":
                return E;
            case "S":
                return S;
            case "W":
                return W;
            default:
                throw new IllegalArgumentException("Invalid direction: " + dir);
        }
    }

    // Returns the direction to the left (90-degree turn left)
    public Direction turnLeft() {
        switch (this) {
            case N:
                return W;
            case E:
                return N;
            case S:
                return E;
            case W:
                return S;
            default:
                throw new IllegalStateException("Invalid direction");
        }
    }

    // Returns the direction to the right (90-degree turn right)
    public Direction turnRight() {
        switch (this) {
            case N:
                return E;
            case E:
                return S;
            case S:
                return W;
            case W:
                return N;
            default:
                throw new IllegalStateException("Invalid direction");
        }
    }

    // String representation ("N", "E", etc.)
    @Override
    public String toString() {
        return this.name();
    }
}
