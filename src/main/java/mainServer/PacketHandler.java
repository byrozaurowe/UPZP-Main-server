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
        Header.Return returned = Header.decode(receivedData);
        if(Serialization.deserialize(returned.bytes, returned.version, socket)) {
            switch((int) returned.version) {
                case 1:
                    break;
                case 2:
                    //klient chce się zalogować
                    //tutaj trzeba rozpakować jego dane i sprawdzić czy istnieje taki zestaw w bazie
                    //zakładam że może się zalogowac i odsyłam listę waiting roomów
                    byte[] bytes = buildWaitingRoomsList(Main.server.waitingRoomsCoordinator.getWaitingRooms());
                    return Header.encode((byte)7, bytes, true);
                case 6:
                    // roomId trzeba odczytać z wiadomości która przyszła, tymczasowo ustawione 1
                    int roomId = 1;
                    return buildWaitingRoom(Main.server.waitingRoomsCoordinator.getWaitingRoom(roomId));
            }
            return null;
        }
        else {
            switch((int) returned.version) {
                case 1:
                    return buildError("Błędne login lub hasło");
                default:
                    return buildError("Błąd");
            }
        }
    }
    private static byte[] buildError(String string) {
        byte[] serialized = Serialization.serialize(string.getBytes(), 1);
        return Header.encode((byte)1, serialized, true);
    }

    private static byte[] buildWaitingRoomsList(ArrayList<WaitingRoom> list) {
        byte[] serialized = Serialization.serialize(list, 7);
        return Header.encode((byte)1, serialized, true);
    }

    private static byte[] buildWaitingRoom(WaitingRoom room) {
        byte[] serialized = Serialization.serialize(room, 8);
        return Header.encode((byte)1, serialized, true);
    }

    private static byte[] bulidTestData(String string) {
        byte[] serialized = Serialization.serialize(string.getBytes(), 1);
        return Header.encode((byte)1, serialized, true);
    }

}
