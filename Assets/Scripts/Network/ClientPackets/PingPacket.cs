public class PingPacket : ClientPacket {
    public PingPacket() : base(0x00) {
        _packetData = new byte[] {0x00, 0x02};
    }
}