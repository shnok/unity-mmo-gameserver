package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.service.GameTimeControllerService;

import static com.shnok.javaserver.config.Configuration.serverConfig;

public class GameTimePacket extends ServerPacket {
    public GameTimePacket() {
        super(ServerPacketType.GameTimePacket.getValue());

        writeL(GameTimeControllerService.getInstance().gameTicks);
        writeI(GameTimeControllerService.getInstance().getTickDurationMs());
        writeI(serverConfig.dayDurationMin());

        buildPacket();
    }
}
