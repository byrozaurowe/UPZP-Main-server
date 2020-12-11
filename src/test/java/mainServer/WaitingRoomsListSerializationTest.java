package mainServer;

import com.google.flatbuffers.FlatBufferBuilder;
import mainServer.schemas.Tester;
import mainServer.schemas.Vec3;
import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class WaitingRoomsListSerializationTest {
    @Test
    public void serialize() throws Exception {
        ArrayList<WaitingRoom> list = new ArrayList<>();
        list.add(new WaitingRoom("Wroclaw", new Client("ziemniak", InetAddress.getAllByName("google.com")[0], new Socket())));
        Serialization.serialize(list, 7);
    }
}
