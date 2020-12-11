package mainServer;

import javax.lang.model.type.NullType;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class WaitingRoomsCoordinator {

    private ArrayList<WaitingRoom> waitingRooms;

    public WaitingRoomsCoordinator() {
        waitingRooms = new ArrayList<WaitingRoom>();
    }

    public void addWaitingRoom(WaitingRoom waitingRoom) {
        waitingRooms.add(waitingRoom);
    }
    public ArrayList<WaitingRoom> getWaitingRooms() { return waitingRooms; }
    public WaitingRoom getWaitingRoom(int roomId) {
        for (WaitingRoom room : waitingRooms) {
            if (room.getId() == roomId) return room;
        }
        return null;
    }
}
