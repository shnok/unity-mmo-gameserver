package com.shnok.javaserver.dto.internal.gameserver;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.internal.GameServerPacketType;
import com.shnok.javaserver.model.network.SessionKey;

public class PlayerAuthRequestPacket extends SendablePacket {
    public PlayerAuthRequestPacket(String account, SessionKey key) {
        super(GameServerPacketType.PlayerAuthRequest.getValue());

        writeS(account);
        writeI(key.playOkID1);
        writeI(key.playOkID2);
        writeI(key.loginOkID1);
        writeI(key.loginOkID2);

        buildPacket();
    }
}
