package com.shnok.javaserver.dto.external.serverpackets.shortcut;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.shortcut.Shortcut;

public class ShortcutRegisterPacket  extends SendablePacket {
    public ShortcutRegisterPacket(Shortcut shortcut) {
        super(ServerPacketType.ShortcutRegister.getValue());

        writeI(shortcut.getType().getValue());
        writeI(shortcut.getSlot() + (shortcut.getPage() * 12));
        switch (shortcut.getType())
        {
            case ITEM: // 1
                writeI(shortcut.getId());
                break;
            case SKILL: // 2
                writeI(shortcut.getId());
                writeI(shortcut.getLevel());
                break;
            case ACTION: // 3
                writeI(shortcut.getId());
                break;
            case MACRO: // 4
                writeI(shortcut.getId());
                break;
            case RECIPE: // 5
                writeI(shortcut.getId());
                break;
            default:
                writeI(shortcut.getId());
        }

        writeI(1);
        
        buildPacket();
    }
}