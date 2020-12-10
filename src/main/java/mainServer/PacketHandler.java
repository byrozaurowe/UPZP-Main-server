package mainServer;

import java.net.Socket;
import java.util.ArrayList;

public class PacketHandler {
    /** Obsługuje pakiet otrzymany od klienta
     * @param receivedData tablica bajtów opisująca pakiet
     * @param socket socket, od którego otrzymano obiekt
     * @throws WrongPacketException błąd podczas odkodowywania pakietu
     */
    static byte[] handleData(byte[] receivedData, Socket socket) throws WrongPacketException {
        Header h = new Header();
        Header.Return returned = h.decode(receivedData);
        Serialization s = new Serialization();
        if(s.deserialize(returned.bytes, returned.version, socket)) {
            switch((int) returned.version) {
                case 1:
                    break;
                case 2:
                    //klient chce się zalogować
                    //tutaj trzeba rozpakować jego dane i sprawdzić czy istnieje taki zestaw w bazie
                    //zakładam że może się zalogowac i odsyłam listę waiting roomów
                    return buildWaitingRoomsList(Main.server.waitingRoomsCoordinator.getWaitingRooms());
                case 6:
                    // roomId trzeba odczytać z wiadomości która przyszła, tymczasowo ustawione 1
                    int roomId = 1;
                    return buildWaitingRoom(Main.server.waitingRoomsCoordinator.getWaitingRoom(roomId));
            }
            return null;
        }
        else {
            byte[] returnMsg;
            switch((int) returned.version) {
                case 1:
                    return buildError("Błędne login lub hasło");
                default:
                    return buildError("Błąd");
            }
        }
    }
    private static byte[] buildError(String string) {
        Serialization s = new Serialization();
        byte[] serialized = s.serialize(string.getBytes(), 1);
        Header h = new Header();
        return h.encode((byte)1, serialized, true);
    }

    private static byte[] buildWaitingRoomsList(ArrayList<WaitingRoom> list) {
        Serialization s = new Serialization();
        byte[] serialized = s.serialize(list, 7);
        Header h = new Header();
        return h.encode((byte)1, serialized, true);
    }

    private static byte[] buildWaitingRoom(WaitingRoom room) {
        Serialization s = new Serialization();
        byte[] serialized = s.serialize(room, 8);
        Header h = new Header();
        return h.encode((byte)1, serialized, true);
    }

    private static byte[] bulidTestData(String string) {
        Serialization s = new Serialization();
        byte[] serialized = s.serialize(string.getBytes(), 1);
        Header h = new Header();
        return h.encode((byte)1, serialized, true);
    }

}
