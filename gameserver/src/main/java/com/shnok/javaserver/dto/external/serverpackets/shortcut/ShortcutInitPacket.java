package com.shnok.javaserver.dto.external.serverpackets.shortcut;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.object.entity.PlayerInstance;

public class ShortcutInitPacket extends SendablePacket {

    public ShortcutInitPacket(PlayerInstance owner) {
        super(ServerPacketType.ShortcutInit.getValue());
    }
}