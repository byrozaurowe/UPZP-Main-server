package mainServer;

import java.io.IOException;
import java.util.ArrayList;

public class WaitingRoomsCoordinator {
    /** Lista waiting roomów */
    private ArrayList<WaitingRoom> waitingRooms;

    /** Wysyła wiadomość do wszystkich w danym waiting roomie
     * @param waitingRoomId id konkretnego waiting roomu
     * @param toSend dane do wysłania
     */
    public void sendToAllInRoom(int waitingRoomId, byte[] toSend) throws IOException {
        getWaitingRoom(waitingRoomId).sendToPlayersInRoom(toSend);
    }

    /** Konstruktor koordynatora waiting roomów */
    public WaitingRoomsCoordinator() {
        waitingRooms = new ArrayList<>();
    }

    public void addWaitingRoom(WaitingRoom waitingRoom) throws IOException {
        // przydzielanie ip, bedzie do zmiany bo baza musi nam wyslac
        if(waitingRooms.isEmpty()) {
            waitingRoom.setId(1);
        }
        else {
            waitingRoom.setId(waitingRooms.get(waitingRooms.size()-1).getId() + 1);
        }
        waitingRooms.add(waitingRoom);
        Main.server.clientsCoordinator.sendToAllWaitingRoomList();
    }

    public ArrayList<WaitingRoom> getWaitingRooms() { return waitingRooms; }

    public WaitingRoom getWaitingRoom(int roomId) {
        for (WaitingRoom room : waitingRooms) {
            if (room.getId() == roomId) return room;
        }
        return null;
    }
}
