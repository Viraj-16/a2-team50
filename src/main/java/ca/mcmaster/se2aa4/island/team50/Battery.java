package ca.mcmaster.se2aa4.island.team50;

class Battery {
    private BatteryLevel batteryLevel;

    public Battery(int initialBattery) {
        this.batteryLevel = new BatteryLevel(initialBattery);
    }

    public void updateBattery(int cost) {
        batteryLevel.drain(cost);
    }

    public boolean isBatteryLow() {
        return batteryLevel.isLow();
    }

    public int getBatteryLevel() {
        return batteryLevel.getLevel();
    }
}
