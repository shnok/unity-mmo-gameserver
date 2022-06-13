using UnityEngine;

public class RequestRotatePacket : ClientPacket {
    public RequestRotatePacket(float angle) : base(0x05) {
        WriteF(angle); 
        BuildPacket();
    }
}