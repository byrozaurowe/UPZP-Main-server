package mainServer;

import java.io.IOException;
import java.util.ArrayList;

public class WaitingRoomsCoordinator {

    private ArrayList<WaitingRoom> waitingRooms;

    /** Wysyła wiadomość do wszystkich w danym waiting roomie
     * @param waitingRoomId id konkretnego waiting roomu
     * @param toSend dane do wysłania
     */
    public void sendToAllInRoom(int waitingRoomId, byte[] toSend) throws IOException {
        getWaitingRoom(waitingRoomId).sendToPlayersInRoom(toSend);
    }

    public WaitingRoomsCoordinator() {
        waitingRooms = new ArrayList<>();
    }

    public void addWaitingRoom(WaitingRoom waitingRoom, int id) {
        waitingRoom.setId(id);
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
