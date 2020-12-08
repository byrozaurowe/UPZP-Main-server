package mainServer;

import com.google.flatbuffers.FlatBufferBuilder;
import mainServer.schemas.Tester;
import mainServer.schemas.Vec3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;

class WrongPacketException extends Exception {
    public WrongPacketException(String errorMessage) {
        super(errorMessage);
    }
}

/** Klasa odpowiedzialna za kodowanie i odkodowywanie pakietów */
public class Header {
    private byte[] BEGINSEQ = CRC.hexStringToLittleEndianByteArray("ABDA");
    private static byte res1 = (byte) 00000001;
    private static byte res = (byte) 00000000;
    private int payloadLength;
    private byte version;
    private boolean isChecksum;

    private byte[] header;
    private byte[] data;

    class Return {
        byte[] bytes;
        byte version;

        Return(byte[] bytes, byte version) {
            this.bytes = bytes;
            this.version = version;
        }
    }

    /** Obsługuje pakiet otrzymany od klienta
     * @param receivedData tablica bajtów opisująca pakiet
     * @throws WrongPacketException błąd podczas odkodowywania pakietu
     */
    static void handleData(byte[] receivedData) throws WrongPacketException {
        Header h = new Header();
        Return returned = h.decode(receivedData);
        Serialization s = new Serialization();
        s.deserialize(returned.bytes, returned.version);
    }

    /** Odkodowywyanie headera
     * @param receivedData otrzymane dane
     * @return przesyłany obiekt
     */
    Return decode(byte[] receivedData) throws WrongPacketException {
        int packetLength;
        if(!Arrays.equals(BEGINSEQ, subArray(receivedData, 0, 1))) {
            throw new WrongPacketException("Wrong begin sequence");
        }
        this.version = receivedData[2];
        this.isChecksum = (receivedData[3] % 2) == 1;

        byte[] payloadLengthTab = subArray(receivedData, 4, 7);
        this.payloadLength = ByteBuffer.wrap(payloadLengthTab).
                                order(ByteOrder.LITTLE_ENDIAN).getInt();
        if(isChecksum)
            packetLength = 16 + payloadLength;
        else
            packetLength = 12 + payloadLength;

        if(packetLength - headerLength() != this.payloadLength)
            throw new WrongPacketException("Wrong payload length");

        header = subArray(receivedData, 0, headerLength()-1);
        data = subArray(receivedData, headerLength(), packetLength-1);

        if(isChecksum) {
            byte[] byteArray = CRC.crc32(data);
            if(!Arrays.equals(byteArray, subArray(header, 8, 11))) {
                throw new WrongPacketException("Wrong payload checksum");
            }
        }

        byte[] calculatedHeaderChecksum = CRC.crc16(subArray(header,
                0, headerLength() - 3));

        if(!Arrays.equals(calculatedHeaderChecksum, subArray(header,
                14, headerLength() - 1))) {
            throw new WrongPacketException("Wrong header checksum");
        }
        return new Return(data, version);
    }

    /** Funkcja kodująca header dla danej tablicy bajtów
     * @param version wersja schematu we flatbuffers
     * @param data tablica bajtów z serializowanym obiektem
     * @param isChecksum czy będzie w headerze zawarte payload checksum
     * @return zapakowane dane w header w formie tablicy bajtów
     */
    byte[] encode(byte version, byte[] data, boolean isChecksum) {
        this.version = version;
        this.isChecksum = isChecksum;
        ArrayList<Byte> encoded = new ArrayList<Byte>();
        encoded.add(BEGINSEQ[0]);
        encoded.add(BEGINSEQ[1]);

        encoded.add(this.version);

        // payload checksum
        if(isChecksum) {
            encoded.add(res1);
            byte[] checksumLength = intToLittleEndian(data.length, 4);
            addAll(checksumLength, encoded);

            byte[] byteArray = CRC.crc32(data);
            addAll(byteArray, encoded);
        }
        else
            encoded.add(res);

        encoded.add(res);
        encoded.add(res);

        header = new byte[headerLength()-2];
        for(int i = 0; i < encoded.size(); i++) {
            header[i] = encoded.get(i);
        }

        byte[] crc = CRC.crc16(header);
        addAll(crc, encoded);

        // adding data
        addAll(data, encoded);

        byte[] result = new byte[encoded.size()];
        for(int i = 0; i < encoded.size(); i++) {
            result[i] = encoded.get(i);
        }

        return result;
    }

    private int headerLength() {
        if(isChecksum){
            return 16;
        }
        return 12;
    }

    private static byte[] intToLittleEndian(int numer, int sizeInByte) {
        ByteBuffer bb = ByteBuffer.allocate(sizeInByte);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(numer);
        return bb.array();
    }

    private void addAll(byte[] tab, ArrayList<Byte> list) {
        for (byte b : tab) {
            list.add(b);
        }
    }

    private byte[] subArray(byte[] array, int beg, int end) {
        byte[] subarray = new byte[end - beg + 1];
        if (subarray.length >= 0) System.arraycopy(array, beg + 0, subarray, 0, subarray.length);
        return subarray;
    }

    public static void main(String[] args) throws Exception {
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
        byte[] encoded = h.encode((byte)1, buf, true);
        if(Arrays.equals(buf, h.decode(encoded).bytes)){
            System.out.println("Poprawnie odkodowano zakodowane dane!");
        }
        else {
            System.out.println("Niepoprawnie odkodowano zakodowane dane!");
        }
    }
}
