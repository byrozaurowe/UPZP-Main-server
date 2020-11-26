package mainServer;

import com.google.flatbuffers.FlatBufferBuilder;
import mainServer.TestData.Client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientTest {
    SocketChannel client;

    ClientTest() {
        try {
            System.out.println("Uruchomienie klienta...");
            client = SocketChannel.open(new InetSocketAddress("localhost", 4444));
            flatbuffers();
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
        System.out.println("mainServer.mainServer.TestData.Client connection closed");
    }

    public void flatbuffers() throws IOException {
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        int name = builder.createString("Wojtek");
        int ip = builder.createString("192.1.2");
        Client.startClient(builder);
        Client.addName(builder, name);
        Client.addIpAddress(builder, ip);
        int test = Client.endClient(builder);
        builder.finish(test);
        sendMessage(builder.dataBuffer());
    }


    public static void main(String[] args) {
        new ClientTest();
    }
}