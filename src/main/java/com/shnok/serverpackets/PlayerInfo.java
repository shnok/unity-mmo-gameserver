package com.shnok.serverpackets;

import com.shnok.model.PlayerInstance;

public class PlayerInfo extends ServerPacket {
    public PlayerInfo(PlayerInstance player) {
        super((byte)0x04);

        //writeS(player.getName());
        //writeI(player.getId());
        //buildPacket();
    }
}
