package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.KeyPacket;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
@Getter
public class ProtocolVersionPacket extends ClientPacket {
    private final int version;
    public ProtocolVersionPacket(GameClientThread client, byte[] data) {
        super(client, data);
        version = readI();

        handlePacket();
    }

    @Override
    public void handlePacket() {
        if (!server.allowedProtocolVersions().contains(getVersion())) {
            log.warn("Received wrong protocol version: {}.", getVersion());

            KeyPacket pk = new KeyPacket(client.enableCrypt(), false);
            client.sendPacket(pk);
            client.setProtocolOk(false);
            client.setCryptEnabled(true);
            client.disconnect();
        } else {
            log.debug("Client protocol version is ok: {}.", getVersion());

            KeyPacket pk = new KeyPacket(client.enableCrypt(), true);
            client.sendPacket(pk);
            client.setProtocolOk(true);
            client.setCryptEnabled(true);
        }
    }
}
