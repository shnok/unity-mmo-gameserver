using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


public class InflictAttackPacket : ClientPacket {
    public InflictAttackPacket(int targetId, byte attackId, int damage) : base(0x07) {
        WriteI(targetId);
        WriteB(attackId);
        WriteI(damage);

        BuildPacket();
    }
}

