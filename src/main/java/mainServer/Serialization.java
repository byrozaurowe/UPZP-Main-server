package mainServer;

import com.google.flatbuffers.FlatBufferBuilder;
import mainServer.schemas.FChooseWaitingRoom.FChooseWaitingRoom;
import mainServer.schemas.FError.FError;
import mainServer.schemas.FGame.FTeam;
import mainServer.schemas.FLoggingClient.FLoggingClient;
import mainServer.schemas.FWaitingRoomsList.FWaitingRoom;
import mainServer.schemas.FWaitingRoomsList.FWaitingRoomsList;
import mainServer.schemas.Tester;
import mainServer.schemas.Vec3;

import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/** Klasa zajmująca się serializacją i deserializacją obiektów z pakietów */
public class Serialization {

    /** Funkcja przeprowadzająca deserializacje danych
     * @param data dane do deserializacji
     * @param version wersja schematu do użycia
     * @param s socket
     * @return
     */
     static Object deserialize(byte[] data, int version, Socket s) {
        switch(version) {
            case 1:
                //deserializeTestData(data);
            case 2:
                return deserializeLoggingClient(data, s);
            case 5:
                return deserializeNewWaitingRoom(data, s);
            case 6:
                return deserializeChooseWaitingRoom(data);
            default:
                deserializeTestData(data);
        }
        return null;
    }

    /** Funkcja przeprowadzająca serializację obiektów
     * @param data dane do serializacji
     * @param version wersja schematu do użycia
     * @return
     */
    static byte[] serialize(Object data, int version) {
        switch(version) {
            case 1:
                return serializeError(data);
            case 2:
                break;
            case 7:
                return serializeWaitingRoomsList(data);
            case 8:
                return serializeWaitingRoom(data);
            default:
                return serializeTestData();
        }
        return null;
    }

    private static boolean deserializeLoggingClient(byte[] data, Socket socket) {
        try {
            FLoggingClient client =
                    FLoggingClient.getRootAsFLoggingClient(ByteBuffer.wrap(data));
            String name = client.name();
            String pass = client.password();
            return Main.server.clientsCoordinator.verifyLoggingClient(
                    socket, name, pass);
        } catch (Exception e) {
            System.out.println("Deserialize loggingClient " + e.getMessage());
            return false;
        }
    }

    private static byte[] serializeError(Object msg) {
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        int message = builder.createString(msg.toString());
        FError.startFError(builder);
        FError.addMessage(builder, message);
        int test = FError.endFError(builder);
        builder.finish(test);
        return builder.sizedByteArray();
    }

    private static byte[] serializeWaitingRoomsList(Object list) {
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        ArrayList<Integer> tab = new ArrayList<>();
        for (WaitingRoom room : (ArrayList<WaitingRoom>) list) {
            int city = builder.createString(room.getCity());
            int serializedRoom = FWaitingRoom.createFWaitingRoom(builder,
                    room.getId(),
                    city,
                    room.getHost(),
                    room.getClientsLoggedVal(),
                    room.getClientsMax(),
                    room.getStatus());
            tab.add(serializedRoom);
        }
        FWaitingRoomsList.startFWaitingRoomsList(builder);
        for (int serializedRoom: tab) {
            FWaitingRoomsList.addWaitingRoom(builder, serializedRoom);
        }
        int readyList = FWaitingRoomsList.endFWaitingRoomsList(builder);
        builder.finish(readyList);
        return builder.sizedByteArray();
    }

    private static byte[] serializeWaitingRoom(Object data) {
        WaitingRoom room = (WaitingRoom)data;
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        mainServer.schemas.FWaitingRoom.FWaitingRoom.startFWaitingRoom(builder);
        mainServer.schemas.FWaitingRoom.FWaitingRoom.addId(builder, room.getId());
        mainServer.schemas.FWaitingRoom.FWaitingRoom.startTeamsVector(builder, 2);
        int[] teams = FTeam.
        mainServer.schemas.FWaitingRoom.FWaitingRoom.createTeamsVector(builder, )
        mainServer.schemas.FWaitingRoom.FWaitingRoom.addTeams(builder,
                mainServer.schemas.FWaitingRoom.FWaitingRoom.createTeamsVector(builder, ));
        */
        return null;
    }

    private static byte[] serializeTestData() {
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        int someString = builder.createString("qwer");
        Tester.startTester(builder);
        Tester.addPos(builder, Vec3.createVec3(builder, 1.0f, 2.0f, 3.0f));
        Tester.addSomeString(builder, someString);
        Tester.addSomeInteger(builder, 123);
        int test = Tester.endTester(builder);
        builder.finish(test);
        byte[] buf = builder.sizedByteArray();
        return Header.encode((byte)1, buf, true);
    }

    private static void deserializeTestData(byte[] data) {
        Tester tester = Tester.getRootAsTester(ByteBuffer.wrap(data));
        int some_integer = tester.someInteger();
        String some_string = tester.someString();
        Vec3 pos = tester.pos();
        float x = pos.x();
        float y = pos.y();
        float z = pos.z();

        System.out.println("Received message: " );
        System.out.println("[Tester]");
        System.out.println("some_integer: " + some_integer);
        System.out.println("some_string: " + some_string);
        System.out.println("x: " + x + "y: " + y + "z: " + z);
    }

    private static Object deserializeChooseWaitingRoom(byte[] data) {
        FChooseWaitingRoom chooseWaitingRoom = FChooseWaitingRoom.getRootAsFChooseWaitingRoom(ByteBuffer.wrap(data));
        int roomId = chooseWaitingRoom.id();
        return Main.server.waitingRoomsCoordinator.getWaitingRoom(roomId);
    }

    private static Object deserializeNewWaitingRoom(byte[] data, Socket s) {
        FNewWaitingRoom newWaitingRoom = FNewWaitingRoom.getRootAsFNewWaitingRoom(ByteBuffer.wrap(data));
        String city = newWaitingRoom.city();
        int clientsMax = newWaitingRoom.clientsMax();
        Client host = Main.server.clientsCoordinator.findClientBySocket(s);
        return new WaitingRoom(city, host, clientsMax);
    }

}
