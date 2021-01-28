package mainServer;

/** Klasa obiektu pojazd */
public class Vehicle {
    /** Enum rodzaju pojazdu */
    public enum VehicleType {
        Car, Cyclist, Pedestrian
    }
    /** Rodzaj pojazdu */
    VehicleType type;
    /** Prędkość pojazdu */
    int velocity;
    /** Czy pojazd jest wolny? */
    boolean free;

    /** Konstruktor pojazdu
     * @param type rodzaj pojazdu
     * @param velocity prędkość pojazdu
     */
    public Vehicle (VehicleType type, int velocity) {
        this.type = type;
        this.velocity = velocity;
        this.free = true;
    }

    /** Zwraca prędkość pojazdu
     * @return prędkość pojazdu jako int
     * */
    public int getVelocity() { return velocity; }

    /** Zwraca rodzaj pojazdu
     * @return rodzaj pojazdu jako enum VehicleType
     */
    public VehicleType getVehicleType() { return type; }
}
