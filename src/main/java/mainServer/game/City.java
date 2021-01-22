package mainServer.game;

public class City {
    /** String nazwy miasta */
    private String cityString;
    /** Szerokość geograficzna */
    private double latitude;
    /** Długość geograficzna mapy */
    private double longitude;
    /** Promień mapy */
    private int radius;

    /**
     * @param latitude Szerokość geograficzna
     * @param longitude Długość geograficzna mapy
     * @param radius Promień mapy
     * @param cityString Nazwa miasta
     */
    public City(double latitude, double longitude, int radius, String cityString) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.cityString = cityString;
    }
}