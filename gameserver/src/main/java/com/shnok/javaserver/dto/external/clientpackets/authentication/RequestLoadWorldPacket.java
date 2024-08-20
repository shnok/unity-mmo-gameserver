package com.shnok.javaserver.dto.external.clientpackets.authentication;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.authentication.GameTimePacket;
import com.shnok.javaserver.enums.network.GameClientState;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.thread.GameClientThread;

public class RequestLoadWorldPacket extends ClientPacket {

    public RequestLoadWorldPacket(GameClientThread client) {
        super(client, new byte[1]);

        handlePacket();
    }

    @Override
    public void handlePacket() {
        client.setClientReady(true);

        client.setGameClientState(GameClientState.IN_GAME);

        // Share character with client
        client.sendPacket(new GameTimePacket());

        // Load and notify surrounding entities
        Point3D spawnPos = client.getCurrentPlayer().getPosition().getWorldPosition();
        client.getCurrentPlayer().setPosition(spawnPos);

        //TODO: is it needed?
        client.getCurrentPlayer().getKnownList().forceRecheckSurroundings();

        client.authenticate();
    }
}
