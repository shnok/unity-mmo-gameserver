using UnityEngine;

public class RequestMovePacket : ClientPacket {

    public RequestMovePacket(Vector3 pos) : base(0x03) {
        WriteI((int) pos.x);
        WriteI((int) pos.y);
        WriteI((int) pos.z);
        BuildPacket();
    }
}