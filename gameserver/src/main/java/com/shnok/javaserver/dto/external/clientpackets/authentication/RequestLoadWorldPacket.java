package com.shnok.javaserver.dto.external.clientpackets.authentication;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.SystemMessagePacket;
import com.shnok.javaserver.dto.external.serverpackets.authentication.GameTimePacket;
import com.shnok.javaserver.dto.external.serverpackets.item.InventoryItemListPacket;
import com.shnok.javaserver.dto.external.serverpackets.shortcut.ShortcutInitPacket;
import com.shnok.javaserver.enums.network.GameClientState;
import com.shnok.javaserver.enums.network.SystemMessageId;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.thread.GameClientThread;

import java.util.Base64;

public class RequestLoadWorldPacket extends ClientPacket {

    public RequestLoadWorldPacket(GameClientThread client) {
        super(client, new byte[1]);

        handlePacket();
    }

    @Override
    public void handlePacket() {
        client.setClientReady(true);

        client.setGameClientState(GameClientState.IN_GAME);

        SystemMessagePacket sm = new SystemMessagePacket(SystemMessageId.WELCOME_TO_LINEAGE);
        client.sendPacket(sm);

        client.sendPacket(new GameTimePacket());

        client.sendPacket(new InventoryItemListPacket(client.getCurrentPlayer(), false));

        client.sendPacket(new ShortcutInitPacket(client.getCurrentPlayer()));

        client.getCurrentPlayer().sendMessage("If you would like to contribute to the project, " +
                "don't hesitate to join the discord server at: https://discord.gg/Z8g6FyXDxy.");

        // Load and notify surrounding entities
        Point3D spawnPos = client.getCurrentPlayer().getPosition().getWorldPosition();
        client.getCurrentPlayer().setPosition(spawnPos);

        //TODO: is it needed?
        client.getCurrentPlayer().getKnownList().forceRecheckSurroundings();

        client.authenticate();
    }

    private String getText(String string) {
        return new String(Base64.getDecoder().decode(string));
    }
}
