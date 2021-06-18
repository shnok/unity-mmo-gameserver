using System.Text;

public class AuthPacket : ClientPacket {

    public AuthPacket(string username) : base(0x01) {
        buildPacket(Encoding.ASCII.GetBytes(username));
    }
}