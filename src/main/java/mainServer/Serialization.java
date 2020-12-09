package mainServer;

import mainServer.schemas.Tester;
import mainServer.schemas.Vec3;
import mainServer.logging.LoggingClient;

import java.nio.ByteBuffer;

public class Serialization {

     Object deserialize(byte[] data, byte version) {
        switch((int) version) {
            case 1:
                buildTestData(data);
            case 2:
                return buildLoggingClient(data);
            default:
                buildTestData(data);
        }
        return null;
    }

    private LoggingClient buildLoggingClient(byte[] data) {
        //LoggingClient client =
                //fLoggingClient.getRootAsLoggingClient(ByteBuffer.wrap(data));

        return null;//new LoggingClient(client.name(), client.password());
    }

    void buildTestData(byte[] data) {
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
