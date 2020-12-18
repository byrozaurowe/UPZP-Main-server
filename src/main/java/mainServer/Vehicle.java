package mainServer;

public class Vehicle {
    public enum VehicleType {
        Car, Cyclist, Pedestrian
    }
    VehicleType type;
    int velocity;
    boolean free;

    public Vehicle (VehicleType type, int velocity) {
        this.type = type;
        this.velocity = velocity;
        this.free = true;
    }

    public int getVelocity() { return velocity; }
    public VehicleType getVehicleType() { return type; }
}
