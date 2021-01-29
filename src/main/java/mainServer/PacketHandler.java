package mainServer;

import mainServer.schemas.FGame.FGame;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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
        byte[] toSend = null;
        Client c = Main.server.clientsCoordinator.findClientBySocket(client.socket());
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
                case 3:
                    byte m = (byte) o;
                    switch(m) {
                        case (byte)0:
                            startGame(c);
                            Main.server.clientsCoordinator.sendToAllWaitingRoomList();
                            break;
                        case (byte)1:
                            WaitingRoom room = Main.server.waitingRoomsCoordinator.getWaitingRoomByClient(c);
                            room.leaveWaitingRoom(c);
                            room.sendToPlayersInRoom(buildWaitingRoom(room));
                            Main.server.clientsCoordinator.sendToAllWaitingRoomList();
                            break;
                    }
                    break;
                case 5:
                    if(o instanceof WaitingRoom) {
                        WaitingRoom room = (WaitingRoom) o;
                        room.joinTeam(c);
                        Main.server.waitingRoomsCoordinator.newWaitingRoom(room);
                        room.sendToPlayersInRoom(buildWaitingRoom(room));
                        Main.server.clientsCoordinator.sendToAllWaitingRoomList();
                    }
                    else {
                        toSend = buildError("Nie można utworzyć gry w takim mieście!");
                    }
                    break;
                case 6:
                    if(c.getClientStatus() == ClientStatus.WAITING_ROOM_LIST) {
                        WaitingRoom room = Main.server.waitingRoomsCoordinator.getWaitingRoom((int) o);
                        if(!room.getStatus()) {
                            if (room.joinTeam(c)) {
                                room.sendToPlayersInRoom(buildWaitingRoom(room));
                            } else {
                                toSend = buildError("Wybrany pokój jest pełny!");
                            }
                        }
                        else {
                            WaitingRoom cloned = Main.server.waitingRoomsCoordinator.cloneWaitingRoom(room, c);
                            if(cloned != null) {
                                room.addClientToRunningGame(buildGame(cloned), c, client, buildGameStarted(room));
                            }
                            else {
                                toSend = buildError("Gra jest pełna!");
                            }
                        }
                    } else {
                        toSend = buildError("Klient już dołączył do innej gry. Opuszczanie...");
                        client.write(ByteBuffer.wrap(toSend));
                        Main.server.waitingRoomsCoordinator.getWaitingRoomByClient(c).leaveWaitingRoom(c);
                        WaitingRoom wr = (WaitingRoom) o;
                        wr.joinTeam(c);
                        wr.sendToPlayersInRoom(buildWaitingRoom(wr));
                        Main.server.clientsCoordinator.sendToAllWaitingRoomList();
                    }
                    break;
                case 8:
                    if(o instanceof WaitingRoom) {
                        WaitingRoom r = (WaitingRoom) o;
                        r.sendToPlayersInRoom(buildWaitingRoom(r));
                    }
                    else toSend = buildError("Nie udało się zmienić pojazdu!");
                    break;
                case 11:
                    if((Boolean) o) {
                        WaitingRoom room = Main.server.waitingRoomsCoordinator.getWaitingRoomBySocket(client.socket());
                        room.endGame();
                    }
                    break;
                case 12:
                    int gameId = (int) o;
                    WaitingRoom room = Main.server.waitingRoomsCoordinator.getWaitingRoom(gameId);
                    room.setGameSocket(client.socket());
                    client.write(ByteBuffer.wrap(buildGame(room)));
                    break;
            }
        }
        if(toSend != null)
            client.write(ByteBuffer.wrap(toSend));
    }

    /** Buduje pakiet błędu
     * @param string treść błędu
     * @return pakiet z headerem
     */
    public static byte[] buildError(String string) {
        byte[] serialized = Serialization.serialize(string, 1);
        return Header.encode((byte)1, serialized, true);
    }

    /** Buduje pakiet listy waiting roomów
     * @return pakiet listy waiting roomów z headerem
     */
    public static byte[] buildWaitingRoomsList() {
        ArrayList<WaitingRoom> list = Main.server.waitingRoomsCoordinator.getWaitingRooms();
        byte[] serialized = Serialization.serialize(list, 7);
        return Header.encode((byte)7, serialized, true);
    }

    /** Buduje pakiet waiting roomu
     * @param room pokój do wysłania
     * @return pakiet waiting roomu z headerem
     */
    private static byte[] buildWaitingRoom(WaitingRoom room) {
        byte[] serialized = Serialization.serialize(room, 4);
        return Header.encode((byte)4, serialized, true);
    }

    /** Buduje game started
     * @param room pokój w którym rozpoczyna się gra
     * @return pakiet game started z headerem
     */
    private static byte[] buildGameStarted(WaitingRoom room) {
        byte[] serialized = Serialization.serialize(room, 9);
        return Header.encode((byte)9, serialized, true);
    }

    /** Rozpoczyna grę
     * @param c host, który ją rozpoczyna
     */
    private static void startGame(Client c) throws IOException {
        WaitingRoom room = Main.server.waitingRoomsCoordinator.getWaitingRoomByClient(c);
        if(room.startGame()) {
            room.sendToPlayersInRoom(buildGameStarted(room));
        }
        else {
            c.getSocket().getChannel().write(ByteBuffer.wrap(buildError("Za mało osób, żeby wystartować grę!")));
        }
    }

    /** Buduje pakiet z grą
     * @param room id gry, która się rozpoczyna
     * @return zserializowana gra z headerem
     */
    static byte[] buildGame(WaitingRoom room) {
        byte[] serialized = Serialization.serialize(room, 10);
        return Header.encode((byte)10, serialized, false);
    }

    public static void main(String[] args) {
        WaitingRoom room = null;
        Client c = null;
        try {
            c = new Client("Wojtek", InetAddress.getByName("127.0.0.1"), new Socket());
            room = new WaitingRoom("Wrocław",c,2);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        room.setId(1);
        room.joinTeam(c);
        byte[] serialized = Serialization.serialize(room, 10);
        FGame game = FGame.getRootAsFGame(ByteBuffer.wrap(serialized));
        game.id();
    }
}
