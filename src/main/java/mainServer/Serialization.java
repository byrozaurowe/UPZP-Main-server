package mainServer;

import com.google.flatbuffers.FlatBufferBuilder;
import mainServer.game.GamesHandler;
import mainServer.schemas.FChooseWaitingRoom.FChooseWaitingRoom;
import mainServer.schemas.FError.FError;
import mainServer.schemas.FGame.FTeam;
import mainServer.schemas.FGame.FVehicle;
import mainServer.schemas.FGameStarted.FGameStarted;
import mainServer.schemas.FLoggingClient.FLoggingClient;
import mainServer.schemas.FNewWaitingRoom.FNewWaitingRoom;
import mainServer.schemas.FWaitingRoom.FClient;
import mainServer.schemas.FWaitingRoom.FVehicleType;
import mainServer.schemas.FWaitingRoomsList.FWaitingRoom;
import mainServer.schemas.FWaitingRoomsList.FWaitingRoomsList;

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

            case 2:
                return deserializeLoggingClient(data, s);
            case 3:
                return deserializeMessage(data, s);
            case 5:
                return deserializeNewWaitingRoom(data, s);
            case 6:
                return deserializeChooseWaitingRoom(data);
            case 8:
                return deserializeVehicle(data, s);

            default:
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
            case 4:
                return serializeWaitingRoom(data);
            case 7:
                return serializeWaitingRoomsList(data);
            case 9:
                return serializeGameStarted(data);
            case 10:
                return serializeGame(data);
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
            int hostName = builder.createString(room.getHostName());
            int serializedRoom = FWaitingRoom.createFWaitingRoom(builder,
                    room.getId(),
                    city,
                    hostName,
                    room.getClientsLoggedVal(),
                    room.getClientsMax(),
                    room.getStatus());
            tab.add(serializedRoom);
        }

        int[] tab1 = new int[tab.size()];
        for(int i = 0; i < tab.size(); i++) {
            tab1[i] = tab.get(i);
        }
        int b = FWaitingRoomsList.createWaitingRoomVector(builder, tab1);
        FWaitingRoomsList.startFWaitingRoomsList(builder);
        FWaitingRoomsList.addWaitingRoom(builder, b);
        int wynik = FWaitingRoomsList.endFWaitingRoomsList(builder);
        builder.finish(wynik);
        return builder.sizedByteArray();
    }

    private static byte[] serializeGameStarted(Object room) {
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        int ip = builder.createString(Main.server.getIp());
        int test = FGameStarted.createFGameStarted(builder, ip, ((WaitingRoom) room).getUdpPort());
        builder.finish(test);
        return builder.sizedByteArray();
    }

    private static byte[] serializeWaitingRoom(Object data) {
        WaitingRoom room = (WaitingRoom)data;
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        int[] serializedTeam = new int[2];
        ArrayList<Integer> serializedClientTab = new ArrayList<>();
        for (Client client : room.getClients(1)) {
            byte vehicleType = 0;
            switch (client.getVehicle().getVehicleType()) {
                case Car: vehicleType = FVehicleType.Car;
                    break;
                case Cyclist: vehicleType = FVehicleType.Cyclist;
                    break;
                case Pedestrian:vehicleType = FVehicleType.Pedestrian;
            }
            int serializedVehicle = mainServer.schemas.FWaitingRoom.FVehicle.createFVehicle(builder, vehicleType, client.getVehicle().getVelocity());
            int serializedName = builder.createString(client.getName());
            int serializedClient = FClient.createFClient(builder, serializedName, client.getId(), serializedVehicle);
            serializedClientTab.add(serializedClient);
        }
        int[] clientTab = new int [serializedClientTab.size()];
        for (int i = 0; i < serializedClientTab.size(); i++) {
            clientTab[i] = serializedClientTab.get(i);
        }
        int b = mainServer.schemas.FWaitingRoom.FTeam.createClientsVector(builder, clientTab);
        mainServer.schemas.FWaitingRoom.FTeam.startFTeam(builder);
        mainServer.schemas.FWaitingRoom.FTeam.addClients(builder, b);
        serializedTeam[0] = FTeam.endFTeam(builder);
        serializedClientTab = new ArrayList<>();
        for (Client client : room.getClients(2)) {
            byte vehicleType = 0;
            switch (client.getVehicle().getVehicleType()) {
                case Car: vehicleType = FVehicleType.Car;
                    break;
                case Cyclist: vehicleType = FVehicleType.Cyclist;
                    break;
                case Pedestrian:vehicleType = FVehicleType.Pedestrian;
            }
            int serializedVehicle = mainServer.schemas.FWaitingRoom.FVehicle.createFVehicle(builder, vehicleType, client.getVehicle().getVelocity());
            int serializedName = builder.createString(client.getName());
            int serializedClient = FClient.createFClient(builder, serializedName, client.getId(), serializedVehicle);
            serializedClientTab.add(serializedClient);
        }
        clientTab = new int [serializedClientTab.size()];
        for (int i = 0; i < serializedClientTab.size(); i++) {
            clientTab[i] = serializedClientTab.get(i);
        }
        b = mainServer.schemas.FWaitingRoom.FTeam.createClientsVector(builder, clientTab);
        mainServer.schemas.FWaitingRoom.FTeam.startFTeam(builder);
        mainServer.schemas.FWaitingRoom.FTeam.addClients(builder, b);
        serializedTeam[1] = FTeam.endFTeam(builder);
        int serializedCity = builder.createString(room.getCity());
        int serializedTeamsVector = mainServer.schemas.FWaitingRoom.FWaitingRoom.createTeamsVector(builder, serializedTeam);
        mainServer.schemas.FWaitingRoom.FWaitingRoom.startFWaitingRoom(builder);
        mainServer.schemas.FWaitingRoom.FWaitingRoom.addId(builder, room.getId());

        mainServer.schemas.FWaitingRoom.FWaitingRoom.addTeams(builder, serializedTeamsVector);
        mainServer.schemas.FWaitingRoom.FWaitingRoom.addCity(builder, serializedCity);
        mainServer.schemas.FWaitingRoom.FWaitingRoom.addHost(builder, room.getHost());
        mainServer.schemas.FWaitingRoom.FWaitingRoom.addClientsMax(builder, room.getClientsMax());
        int serializedRoom = mainServer.schemas.FWaitingRoom.FWaitingRoom.endFWaitingRoom(builder);
        builder.finish((serializedRoom));
        return builder.sizedByteArray();
    }

    private static byte[] serializeGame(Object data) {
        WaitingRoom room = (WaitingRoom)data;
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        int[] serializedTeam = new int[2];
        ArrayList<Integer> serializedClientTab = new ArrayList<>();
        for (Client client : room.getClients(1)) {
            byte vehicleType = 0;
            switch (client.getVehicle().getVehicleType()) {
                case Car: vehicleType = mainServer.schemas.FGame.FVehicleType.Car;
                    break;
                case Cyclist: vehicleType = mainServer.schemas.FGame.FVehicleType.Cyclist;
                    break;
                case Pedestrian:vehicleType = mainServer.schemas.FGame.FVehicleType.Pedestrian;
            }
            int serializedVehicle = FVehicle.createFVehicle(builder, vehicleType);
            int serializedName = builder.createString(client.getName());
            int serializedIpAddress = builder.createString(client.getIpAddress().getHostAddress());
            int serializedClient = mainServer.schemas.FGame.FClient.createFClient(builder, serializedName, client.getId(), serializedIpAddress, client.getSocket().getPort(), serializedVehicle);
            serializedClientTab.add(serializedClient);
        }
        int[] clientTab = new int [serializedClientTab.size()];
        for (int i = 0; i < serializedClientTab.size(); i++) {
            clientTab[i] = serializedClientTab.get(i);
        }
        int b = mainServer.schemas.FGame.FTeam.createClientsVector(builder, clientTab);
        mainServer.schemas.FGame.FTeam.startFTeam(builder);
        mainServer.schemas.FGame.FTeam.addClients(builder, b);
        serializedTeam[0] = FTeam.endFTeam(builder);
        serializedClientTab = new ArrayList<>();
        for (Client client : room.getClients(2)) {
            byte vehicleType = 0;
            switch (client.getVehicle().getVehicleType()) {
                case Car: vehicleType = mainServer.schemas.FGame.FVehicleType.Car;
                    break;
                case Cyclist: vehicleType = mainServer.schemas.FGame.FVehicleType.Cyclist;
                    break;
                case Pedestrian:vehicleType = mainServer.schemas.FGame.FVehicleType.Pedestrian;
            }
            int serializedVehicle = FVehicle.createFVehicle(builder, vehicleType);
            int serializedName = builder.createString(client.getName());
            int serializedIpAddress = builder.createString(client.getIpAddress().getHostAddress());
            int serializedClient = mainServer.schemas.FGame.FClient.createFClient(builder, serializedName, client.getId(), serializedIpAddress, client.getSocket().getPort(), serializedVehicle);
            serializedClientTab.add(serializedClient);
        }
        clientTab = new int [serializedClientTab.size()];
        for (int i = 0; i < serializedClientTab.size(); i++) {
            clientTab[i] = serializedClientTab.get(i);
        }
        b = mainServer.schemas.FGame.FTeam.createClientsVector(builder, clientTab);
        mainServer.schemas.FGame.FTeam.startFTeam(builder);
        mainServer.schemas.FGame.FTeam.addClients(builder, b);
        serializedTeam[1] = FTeam.endFTeam(builder);
        mainServer.schemas.FGame.FGame.startFGame(builder);
        mainServer.schemas.FGame.FGame.addId(builder, room.getId());
        int serializedTeamsVector = mainServer.schemas.FGame.FGame.createTeamsVector(builder, serializedTeam);
        mainServer.schemas.FGame.FGame.addTeams(builder, serializedTeamsVector);
        int serializedRoom = mainServer.schemas.FGame.FGame.endFGame(builder);
        builder.finish((serializedRoom));
        return builder.sizedByteArray();
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
        if(GamesHandler.cities.contains(city)) {
            return new WaitingRoom(city, host, clientsMax);
        } else {
            return false;
        }
    }

    private static Object deserializeVehicle(byte[] data, Socket s) {
        mainServer.schemas.FWaitingRoom.FVehicle vehicle = mainServer.schemas.FWaitingRoom.FVehicle.getRootAsFVehicle(ByteBuffer.wrap(data));

        Vehicle.VehicleType type = null;
        switch (vehicle.vehicleType()) {
            case 0:
                type = Vehicle.VehicleType.Pedestrian;
                break;
            case 1:
                type = Vehicle.VehicleType.Cyclist;
                break;
            case 2:
                type = Vehicle.VehicleType.Car;
                break;
        }
        int velocity = vehicle.velocity();

        Client client = Main.server.clientsCoordinator.findClientBySocket(s);
        WaitingRoom room = Main.server.waitingRoomsCoordinator.getWaitingRoomByClient(client);
        Team team = room.getTeamByClient(client);
        boolean isChanged = team.changeVehicle(type, velocity, client);

        if (isChanged) {
            return room;
        }
        return false;
    }

    private static Object deserializeMessage(byte[] data, Socket s) {
        mainServer.schemas.FMessage.FMessage message = mainServer.schemas.FMessage.FMessage.getRootAsFMessage(ByteBuffer.wrap(data));
        return message.messageType();
    }

    private static Object deserializeStartGame(byte[] data, Socket s) {
        //tutaj trzeba wystartować nową grę, pobrać od podprocesu port i ip i wysłać je do wszystkich użytkowników w waiting roomie
        return null;
    }

}
