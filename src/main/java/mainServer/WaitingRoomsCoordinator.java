package mainServer;

import java.io.IOException;
import java.sql.SQLException;
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

    /** Usuwa konkretny waiting room
     * @param w waiting room do usunięcia
     */
    public void removeWaitingRoom(WaitingRoom w) {
        waitingRooms.remove(w);
    }

    /** Tworzenie nowego waiting roomu w trakcie działania serwera
     * @param waitingRoom pokój do utworzenia
     */
    public void newWaitingRoom(WaitingRoom waitingRoom) throws IOException, SQLException {
        // przydzielanie id, bedzie do zmiany bo baza musi nam wyslac
        int id = DatabaseHandler.getInstance().getFreeGameId();
        if(waitingRooms.isEmpty()) {
            waitingRoom.setId(1);
        }
        else {
            waitingRoom.setId(id + 1);
        }
        waitingRooms.add(waitingRoom);
        Main.server.clientsCoordinator.sendToAllWaitingRoomList();
    }

    /** Dodanie waiting roomu na początku działania serwera
     * @param waitingRoom pokój do dodania
     */
    public void addWaitingRoom(WaitingRoom waitingRoom) throws SQLException {
        int id = DatabaseHandler.getInstance().getFreeGameId();
        if(waitingRooms.isEmpty()) {
            waitingRoom.setId(1);
        }
        else {
            waitingRoom.setId(id + 1);
        }
        waitingRooms.add(waitingRoom);
    }

    public ArrayList<WaitingRoom> getWaitingRooms() { return waitingRooms; }

    public WaitingRoom getWaitingRoom(int roomId) {
        for (WaitingRoom room : waitingRooms) {
            if (room.getId() == roomId) return room;
        }
        return null;
    }

    public WaitingRoom getWaitingRoomByClient(Client client) {
        for (WaitingRoom room : waitingRooms) {
            if (room.isClientInRoom(client)) return room;
        }
        return null;
    }
}
