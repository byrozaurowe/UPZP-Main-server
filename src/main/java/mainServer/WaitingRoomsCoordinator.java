package mainServer;

import javax.lang.model.type.NullType;
import java.util.ArrayList;

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
