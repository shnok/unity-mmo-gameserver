public class LoadWorldPacket : ClientPacket {
    public LoadWorldPacket() : base(0x04) {
       SetData(new byte[] {0x04, 0x02});
    }
}