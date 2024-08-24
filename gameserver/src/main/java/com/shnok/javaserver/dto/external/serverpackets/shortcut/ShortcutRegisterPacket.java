package com.shnok.javaserver.dto.external.serverpackets.shortcut;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.shortcut.Shortcut;

public class ShortcutRegisterPacket  extends SendablePacket {
    public ShortcutRegisterPacket(Shortcut shortcut) {
        super(ServerPacketType.ShortcutRegister.getValue());
    }
}