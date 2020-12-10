package mainServer;

import java.net.Socket;

public class PacketHandler {
    /** Obsługuje pakiet otrzymany od klienta
     * @param receivedData tablica bajtów opisująca pakiet
     * @param socket socket, od którego otrzymano obiekt
     * @throws WrongPacketException błąd podczas odkodowywania pakietu
     */
    static byte[] handleData(byte[] receivedData, Socket socket) throws WrongPacketException {
        Header h = new Header();
        Header.Return returned = h.decode(receivedData);
        Serialization s = new Serialization();
        if(s.deserialize(returned.bytes, returned.version, socket)) {
            switch((int) returned.version) {
                case 1:
                    break;
                case 2:
                    return null;
            }
            return null;
        }
        else {
            byte[] returnMsg;
            switch((int) returned.version) {
                case 1:
                    return buildError("Błędne login lub hasło");
                default:
                    return buildError("Błąd");
            }
        }
    }
    private static byte[] buildError(String string) {
        Serialization s = new Serialization();
        byte[] serialized = s.serialize(string.getBytes(), 1);
        Header h = new Header();
        return h.encode((byte)1, serialized, true);
    }
    private static byte[] bulidTestData(String string) {
        Serialization s = new Serialization();
        byte[] serialized = s.serialize(string.getBytes(), 1);
        Header h = new Header();
        return h.encode((byte)1, serialized, true);
    }

}
