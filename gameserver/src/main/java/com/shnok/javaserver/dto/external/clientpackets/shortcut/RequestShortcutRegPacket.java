package com.shnok.javaserver.dto.external.clientpackets.shortcut;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.shortcut.ShortcutRegisterPacket;
import com.shnok.javaserver.enums.ShortcutType;
import com.shnok.javaserver.model.shortcut.Shortcut;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Getter;

@Getter
public class RequestShortcutRegPacket  extends ClientPacket {

    private ShortcutType type;
    private int id;
    private int slot;
    private int page;
    private int lvl;
    private int characterType; // 1 - player, 2 - pet

    public RequestShortcutRegPacket(GameClientThread client, byte[] data) {
        super(client, data);

        final int typeId = readI();
        type = ShortcutType.values()[(typeId < 1) || (typeId > 6) ? 0 : typeId];
        final int tmp = readI();
        slot = tmp % 12;
        page = tmp / 12;
        id = readI();
        lvl = readI();

        handlePacket();
    }

    @Override
    public void handlePacket() {
        if (client.getCurrentPlayer() == null || (page > 10) || (page < 0)) {
            return;
        }

        final Shortcut sc = new Shortcut(slot, page, type, id, lvl, characterType);
        client.getCurrentPlayer().registerShortCut(sc);
        client.sendPacket(new ShortcutRegisterPacket(sc));
    }
}