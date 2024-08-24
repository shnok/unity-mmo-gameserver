package com.shnok.javaserver.dto.external.clientpackets.shortcut;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.shortcut.ShortcutRegisterPacket;
import com.shnok.javaserver.enums.ShortcutType;
import com.shnok.javaserver.model.shortcut.Shortcut;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Getter;

@Getter
public class RequestShortcutRegPacket  extends ClientPacket {

    private final ShortcutType shortcutType;
    private final int id;
    private final int slot;
    private final int page;
    private final int lvl;
    private int characterType; // 1 - player, 2 - pet

    public RequestShortcutRegPacket(GameClientThread client, byte[] data) {
        super(client, data);

        final int typeId = readI();
        shortcutType = ShortcutType.getById(typeId);
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

        final Shortcut sc = new Shortcut(slot, page, shortcutType, id, lvl, characterType);
        client.getCurrentPlayer().registerShortCut(sc);
        client.sendPacket(new ShortcutRegisterPacket(sc));
    }
}