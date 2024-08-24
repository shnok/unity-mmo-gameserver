package com.shnok.javaserver.dto.external.serverpackets.shortcut;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.model.shortcut.Shortcut;

public class ShortcutInitPacket extends SendablePacket {

    public ShortcutInitPacket(PlayerInstance owner) {
        super(ServerPacketType.ShortcutInit.getValue());

        if (owner == null) {
            return;
        }

        Shortcut[] shortCuts = owner.getAllShortCuts();

        writeI(shortCuts.length);

        for (Shortcut sc : shortCuts) {
            writeI(sc.getType().getValue());
            writeI(sc.getSlot() + (sc.getPage() * 12));

            switch (sc.getType()) {
                case ITEM: // 1
                    writeI(sc.getId());
                    break;
                case SKILL: // 2
                    writeI(sc.getId());
                    writeI(sc.getLevel());
                    break;
                case ACTION: // 3
                    writeI(sc.getId());
                    break;
                case MACRO: // 4
                    writeI(sc.getId());
                    break;
                case RECIPE: // 5
                    writeI(sc.getId());
                    break;
                default:
                    writeI(sc.getId());
            }
        }
        
        buildPacket();
    }
}