package com.shnok.javaserver.dto.internal.gameserver;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.GameServerPacketType;

public class ReplyCharactersPacket extends SendablePacket {
    public ReplyCharactersPacket(String account, int charCount) {
        super(GameServerPacketType.ReplyCharacters.getValue());

        writeS(account);
        writeB((byte) charCount);

        buildPacket();
    }
}
