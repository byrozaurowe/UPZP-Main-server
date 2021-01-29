package mainServer;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

/** Klasa obiektu zajmującego się koordynowaniem waiting roomów */
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
        if(id > 0) {
            waitingRoom.setId(Math.max(id+1, maxId()+1));
        }
        else {
            waitingRoom.setId(Math.max(1, maxId()+1));
        }
        waitingRooms.add(waitingRoom);
        Main.server.clientsCoordinator.sendToAllWaitingRoomList();
    }

    /** Zwraca listę waiting roomów
     * @return lista waiting roomów
     */
    public ArrayList<WaitingRoom> getWaitingRooms() { return waitingRooms; }

    /** Zwraca waiting room
     * @param roomId id żądanego waiting roomu
     * @return żądany waiting room
     */
    public WaitingRoom getWaitingRoom(int roomId) {
        for (WaitingRoom room : waitingRooms) {
            if (room.getId() == roomId) return room;
        }
        return null;
    }

    /** Zwraca waiting room, w którym jest podany klient
     * @param client klient, którego pokoju szukamy
     * @return szukany pokój
     */
    public WaitingRoom getWaitingRoomByClient(Client client) {
        for (WaitingRoom room : waitingRooms) {
            if (room.isClientInRoom(client)) return room;
        }
        return null;
    }

    /** Szuka waiting roomu na podstawie socketa
     * @param s socket do komunikacji z grą
     * @return szukany pokój
     */
    public WaitingRoom getWaitingRoomBySocket(Socket s) {
        for (WaitingRoom room : waitingRooms) {
            if (room.getGameSocket() == s) return room;
        }
        return null;
    }

    /** Tworzenie kopii obiektu waitingroom ale z jedym graczem, który dołącza w trakcie
     * @param room
     * @param client
     * @return
     */
    public WaitingRoom cloneWaitingRoom(WaitingRoom room, Client client) {
        WaitingRoom new_room = new WaitingRoom(room.getCity(), room.getHost(), room.getClientsMax(), room.getPointMax());
        new_room.setId(room.getId());
        int team = room.checkTeamToJoin();
        if(team == -1)
            return null;
        new_room.joinTeam(client, team);
        return new_room;
    }

    public void kickFromWaitingRoom(Client c) {
        WaitingRoom wr = getWaitingRoomByClient(c);
        wr.leaveWaitingRoom(c);
    }

    private int maxId() {
        int max = 0;
        for(WaitingRoom w : waitingRooms) {
            if (w.getId() > max)
                max = w.getId();
        }
        return max;
    }
}
