using System.Text;

public class AuthRequestPacket : ClientPacket {
    public AuthRequestPacket(string username) : base(0x01) {
        WriteS(username);
        BuildPacket();
    }
}