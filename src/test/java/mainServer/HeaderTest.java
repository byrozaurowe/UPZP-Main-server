package mainServer;

import com.google.flatbuffers.FlatBufferBuilder;
import mainServer.schemas.Tester;
import mainServer.schemas.Vec3;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

class HeaderTest {
    @Test
    void decode() throws WrongPacketException {
        FlatBufferBuilder builder = new FlatBufferBuilder(0);
        int someString = builder.createString("qwer");
        Tester.startTester(builder);
        Tester.addPos(builder, Vec3.createVec3(builder, 1.0f, 2.0f, 3.0f));
        Tester.addSomeString(builder, someString);
        Tester.addSomeInteger(builder, 123);
        int test = Tester.endTester(builder);
        builder.finish(test);
        byte[]buf = builder.sizedByteArray();
        Header h = new Header();
        byte[]encoded = h.encode((byte)1, buf, true);
        Assert.assertArrayEquals(buf, h.decode(encoded).bytes);
    }
}