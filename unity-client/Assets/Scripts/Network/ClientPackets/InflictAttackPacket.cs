using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


public class InflictAttackPacket : ClientPacket {
    public InflictAttackPacket(int targetId, AttackType type) : base(0x07) {
        WriteI(targetId);
        WriteB((byte)type);

        BuildPacket();
    }
}

