using UnityEngine;

public class RequestMovePacket : ClientPacket {

    public RequestMovePacket(Vector3 pos) : base(0x03) {
        WriteF(pos.x);
        WriteF(pos.y);
        WriteF(pos.z);
        
        BuildPacket();
    }
}