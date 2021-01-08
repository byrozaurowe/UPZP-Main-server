package mainServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.SQLException;
import java.util.ArrayList;

/** Klasa obsługująca otrzymany pakiet od klienta */
public class PacketHandler {
    /** Obsługuje pakiet otrzymany od klienta
     * @param receivedData tablica bajtów opisująca pakiet
     * @param client socketChannel, od którego otrzymano obiekt
     * @throws WrongPacketException błąd podczas odkodowywania pakietu
     */
    static void handleData(byte[] receivedData, SocketChannel client) throws WrongPacketException, IOException, SQLException {
        Header.Return returned = Header.decode(receivedData);
        Object o = Serialization.deserialize(returned.bytes, returned.version, client.socket());
        byte[] toSend = new byte[0];
        if(o != null) {
            switch((int) returned.version) {
                case 1:
                    break;
                case 2:
                    if(o instanceof Boolean && !(boolean) o) {
                        toSend = buildError("Błędny login lub hasło");
                    } else {
                        toSend = buildWaitingRoomsList();
                    }
                    break;
                case 5:
                    WaitingRoom room = (WaitingRoom) o;
                    Main.server.waitingRoomsCoordinator.newWaitingRoom(room);
                    room.joinTeam(Main.server.clientsCoordinator.findClientBySocket(client.socket()));
                    toSend = buildWaitingRoom(room);
                    break;
                case 6:
                    WaitingRoom wr = (WaitingRoom) o;
                    toSend = buildWaitingRoom(wr);
                    wr.joinTeam(Main.server.clientsCoordinator.findClientBySocket(client.socket()));
                    break;
                case 8:
                    if ((boolean) o) {
                        WaitingRoom r = (WaitingRoom) o;
                        r.sendToPlayersInRoom(buildWaitingRoom(r));
                    }
                    else toSend = buildError("vehicle not changed");
            }
        }
        client.write(ByteBuffer.wrap(toSend));
    }

    /** Buduje pakiet błędu
     * @param string treść błędu
     * @return pakiet z headerem
     */
    public static byte[] buildError(String string) {
        byte[] serialized = Serialization.serialize(string.getBytes(), 1);
        return Header.encode((byte)1, serialized, true);
    }

    public static byte[] buildWaitingRoomsList() {
        ArrayList<WaitingRoom> list = Main.server.waitingRoomsCoordinator.getWaitingRooms();
        byte[] serialized = Serialization.serialize(list, 7);
        return Header.encode((byte)7, serialized, true);
    }

    private static byte[] buildWaitingRoom(WaitingRoom room) {
        byte[] serialized = Serialization.serialize(room, 4);
        return Header.encode((byte)8, serialized, true);
    }
}
