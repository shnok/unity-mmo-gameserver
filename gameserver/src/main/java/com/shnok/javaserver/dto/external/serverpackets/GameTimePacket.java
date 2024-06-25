package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.service.GameTimeControllerService;

import static com.shnok.javaserver.config.Configuration.server;

public class GameTimePacket extends SendablePacket {
    public GameTimePacket() {
        super(ServerPacketType.GameTimePacket.getValue());

        writeL(GameTimeControllerService.getInstance().gameTicks);
        writeI(GameTimeControllerService.getInstance().getTickDurationMs());
        writeI(server.dayDurationMin());

        buildPacket();
    }
}
