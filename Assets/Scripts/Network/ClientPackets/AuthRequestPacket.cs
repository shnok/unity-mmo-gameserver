using System.Text;

public class AuthRequestPacket : ClientPacket {
    public AuthRequestPacket(string username) : base(0x01) {
        //BuildPacket(Encoding.ASCII.GetBytes(username));
        WriteS(username);
        BuildPacket();
    }
}