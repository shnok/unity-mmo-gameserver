using UnityEngine;

public class RequestAnimPacket : ClientPacket {

    public RequestAnimPacket(int anim, float value) : base(0x06) {
        WriteI(anim);
        WriteF(value);

        BuildPacket();
    }
}
