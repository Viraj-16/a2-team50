package ca.mcmaster.se2aa4.island.team50;

class BatteryLevel {
    private int powerLevel;

    public BatteryLevel(int initialLevel) {
        if (initialLevel < 0) {
            throw new IllegalArgumentException("Battery level cannot be negative");
        }
        this.powerLevel = initialLevel;
    }

    public void drain(int cost) {
        this.powerLevel = Math.max(0, this.powerLevel - cost); // Ensure battery does not go negative
    }

    public boolean isLow() {
        return this.powerLevel < 500;
    }

    public int getLevel() {
        int powerLevelCopy = this.powerLevel;
        return powerLevelCopy;
    }
}
