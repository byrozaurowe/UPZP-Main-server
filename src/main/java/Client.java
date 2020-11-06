import com.google.flatbuffers.FlatBufferBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import TestData.Tester;
import TestData.Vec3;

public class Client {
    SocketChannel client;

    Client() {
        try {
            int[] messages = {1, 2};
            System.out.println("Uruchomienie klienta...");
            client = SocketChannel.open(new InetSocketAddress("178.43.130.211", 4444));
            for(int i = 0; i<2; i++){
                flatbuffers(i);
            }
            while(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(ByteBuffer msg) throws IOException {
        System.out.println("Prepared message: " + msg);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(msg);
        buffer.flip();
        int bytesWritten = client.write(buffer);
        System.out.println(String.format("Sending Message: %s\nbufforBytes: %d", msg, bytesWritten));
    }

    public void close() throws IOException {
        client.close();
        System.out.println("Client connection closed");
    }

    public void flatbuffers(int someInt) throws IOException {
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        int someString = builder.createString("Testowy String");
        Tester.startTester(builder);
        Tester.addPos(builder, Vec3.createVec3(builder, 1.0f, 2.0f, 3.0f));
        Tester.addSomeString(builder, someString);
        Tester.addSomeInteger(builder, someInt);
        int test = Tester.endTester(builder);
        builder.finish(test);
        sendMessage(builder.dataBuffer());
    }

    public static void main(String[] args) {
        new Client();
    }
}