using UnityEngine;

public class RequestAnimPacket : ClientPacket {

    public RequestAnimPacket(byte anim, float value) : base(0x06) {
        WriteB(anim);
        WriteF(value);

        BuildPacket();
    }
}
