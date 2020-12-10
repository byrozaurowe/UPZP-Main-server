package mainServer;

import com.google.flatbuffers.FlatBufferBuilder;
import mainServer.schemas.FError.FError;
import mainServer.schemas.FLoggingClient.FLoggingClient;
import mainServer.schemas.Tester;
import mainServer.schemas.Vec3;

import java.net.Socket;
import java.nio.ByteBuffer;

public class Serialization {

     boolean deserialize(byte[] data, byte version, Socket s) {
        switch((int) version) {
            case 1:
                //deserializeTestData(data);
            case 2:
                return deserializeLoggingClient(data, s);
            default:
                deserializeTestData(data);
        }
        return false;
    }

    byte[] serialize(byte[] data, int version) {
        switch(version) {
            case 1:
                return serializeError(data);
            case 2:
                break;
            default:
                return serializeTestData();
        }
        return null;
    }

    private boolean deserializeLoggingClient(byte[] data, Socket socket) {
        try {
            FLoggingClient client =
                    FLoggingClient.getRootAsFLoggingClient(ByteBuffer.wrap(data));
            String name = client.name();
            String pass = client.password();
            return Main.server.clientsCoordinator.verifyLoggingClient(
                    socket, name, pass);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    private byte[] serializeError(byte[] msg) {
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        int message = builder.createString(msg.toString());
        FError.startFError(builder);
        FError.addMessage(builder, message);
        int test = FError.endFError(builder);
        builder.finish(test);
        return builder.sizedByteArray();
    }

    private byte[] serializeTestData() {
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        int someString = builder.createString("qwer");
        Tester.startTester(builder);
        Tester.addPos(builder, Vec3.createVec3(builder, 1.0f, 2.0f, 3.0f));
        Tester.addSomeString(builder, someString);
        Tester.addSomeInteger(builder, 123);
        int test = Tester.endTester(builder);
        builder.finish(test);
        byte[] buf = builder.sizedByteArray();
        Header h = new Header();
        return h.encode((byte)1, buf, true);
    }

    void deserializeTestData(byte[] data) {
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
}
