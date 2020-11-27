package mainServer;

import java.util.ArrayList;

public class WaitingRoomsCoordinator {

    private ArrayList<WaitingRoom> waitingRooms;

    public WaitingRoomsCoordinator() {
        waitingRooms = new ArrayList<WaitingRoom>();
    }

    public void addWaitingRoom(WaitingRoom waitingRoom) {
        waitingRooms.add(waitingRoom);
    }
}
