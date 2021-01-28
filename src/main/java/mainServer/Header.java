package mainServer;

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
    /** Sekwencja rozpoczynająca header */
    private static final byte[] BEGINSEQ = CRC.hexStringToLittleEndianByteArray("ABDA");
    /** Jedynka jako bajt */
    private static byte res1 = (byte)1;
    /** Zero jako bajt */
    private static byte res = (byte)0;

    /** Klasa return*/
    static class Return {
        /** Zserializowany obiekt */
        byte[] bytes;
        /** Wersja schematu obiektu */
        byte version;

        /** Konstruktor
         * @param bytes zserializowany obiekt
         * @param version wersja schematu obiektu
         */
        Return(byte[] bytes, byte version) {
            this.bytes = bytes;
            this.version = version;
        }
    }

    /** Odkodowywyanie headera
     * @param receivedData otrzymane dane
     * @return przesyłany obiekt
     */
    static Return decode(byte[] receivedData) throws WrongPacketException {
        int packetLength;
        if(!Arrays.equals(BEGINSEQ, subArray(receivedData, 0, 1))) {
            throw new WrongPacketException("Wrong begin sequence");
        }
        byte version = receivedData[2];
        boolean isChecksum = (receivedData[3] % 2) == 1;

        byte[] payloadLengthTab = subArray(receivedData, 4, 7);
        int payloadLength = ByteBuffer.wrap(payloadLengthTab).
                                order(ByteOrder.LITTLE_ENDIAN).getInt();
        if(isChecksum)
            packetLength = 16 + payloadLength;
        else
            packetLength = 12 + payloadLength;

        if(packetLength - headerLength(isChecksum) != payloadLength)
            throw new WrongPacketException("Wrong payload length");

        byte[] header = subArray(receivedData, 0, headerLength(isChecksum)-1);
        byte[] data = subArray(receivedData, headerLength(isChecksum), packetLength-1);

        if(isChecksum) {
            byte[] byteArray = CRC.crc32(data);
            if(!Arrays.equals(byteArray, subArray(header, 8, 11))) {
                throw new WrongPacketException("Wrong payload checksum");
            }
        }

        byte[] calculatedHeaderChecksum = CRC.crc16(subArray(header,
                0, headerLength(isChecksum) - 3));

        if(!Arrays.equals(calculatedHeaderChecksum, subArray(header,
                headerLength(isChecksum) - 2, headerLength(isChecksum) - 1))) {
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
    static byte[] encode(byte version, byte[] data, boolean isChecksum) {
        ArrayList<Byte> encoded = new ArrayList<>();
        encoded.add(BEGINSEQ[0]);
        encoded.add(BEGINSEQ[1]);

        encoded.add(version);

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

        byte[] header = new byte[headerLength(isChecksum)-2];
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

    /** Zwraca długość headera w zależności od tego czy załączono checksumę
     * @param isChecksum czy załączono checksumę?
     * @return oczekiwana długość headera
     */
    private static int headerLength(boolean isChecksum) { return isChecksum ? 16 : 12; }

    /** Zamienia int na bajt w little endian
     * @param numer numer w formacie int
     * @param sizeInByte docelowy rozmiar w bajtach
     * @return bajt w little endian
     */
    private static byte[] intToLittleEndian(int numer, int sizeInByte) {
        ByteBuffer bb = ByteBuffer.allocate(sizeInByte);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt(numer);
        return bb.array();
    }

    /** Dodaje wszystkie elementy tablicy tab do listy list
     * @param tab tablica
     * @param list docelowa lista
     */
    private static void addAll(byte[] tab, ArrayList<Byte> list) {
        for (byte b : tab) {
            list.add(b);
        }
    }

    /** Zwraca podtablicę
     * @param array tablica początkowa
     * @param beg początkowy indeks w tablicy
     * @param end końcowy indeks w tablicy
     * @return podtablica
     */
    private static byte[] subArray(byte[] array, int beg, int end) {
        byte[] subarray = new byte[end - beg + 1];
        if (subarray.length >= 0) System.arraycopy(array, beg, subarray, 0, subarray.length);
        return subarray;
    }
}
