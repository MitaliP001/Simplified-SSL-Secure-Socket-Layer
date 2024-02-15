package security;

public class Hash {
    public int calculateChecksum(byte[] data) {
        int checksum = 0;
        for (byte b : data) {
            checksum += b;
        }
        return checksum;
    }

    public byte[] assemblePacket(byte[] data, int checksum) {
        byte[] packet = new byte[data.length + 4];
        System.arraycopy(data, 0, packet, 0, data.length);
        packet[data.length] = (byte) (checksum >> 24);
        packet[data.length + 1] = (byte) (checksum >> 16);
        packet[data.length + 2] = (byte) (checksum >> 8);
        packet[data.length + 3] = (byte) checksum;
        return packet;
    }

    public boolean verifyChecksum(byte[] packet) {
        int receivedChecksum = ((packet[packet.length - 4] & 0xFF) << 24) |
                               ((packet[packet.length - 3] & 0xFF) << 16) |
                               ((packet[packet.length - 2] & 0xFF) << 8) |
                               (packet[packet.length - 1] & 0xFF);

        byte[] data = new byte[packet.length - 4];
        System.arraycopy(packet, 0, data, 0, data.length);

        int calculatedChecksum = calculateChecksum(data);

        return receivedChecksum == calculatedChecksum;
    }
}
