package mainServer;

public class Vehicle {
    public enum VehicleType {
        CAR, CYCLIST, PEDESTRIAN;
    }
    Enum<VehicleType> type;
    int velocity;
    boolean free;

    public Vehicle (VehicleType type, int velocity) {
        this.type = type;
        this.velocity = velocity;
        this.free = true;
    }
}
