package mainServer;

public class Packet {
    public static final String CONNECT = "CONNECT";
    public static final String DISCONNECT = "DISCONNECT";
    public static final String LOGIN = "LOGIN";
    public static final String LOGIN_ACCEPTED = "ACCEPTED";
    public static final String LOGIN_REJECTED = "REJECTED";

    private String packetType;

    private Object toSend;

    Packet(String packetType, Object o) {
        this.packetType = packetType;
        this.toSend = o;
    }


}
