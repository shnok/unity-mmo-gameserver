package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.service.GameTimeControllerService;

public class GameTimePacket extends ServerPacket {
    public GameTimePacket() {
        super(ServerPacketType.GameTimePacket.getValue());

        writeL(GameTimeControllerService.getInstance().gameTicks);
        writeI(GameTimeControllerService.getInstance().getTickDurationMs());
        writeI(Config.TIME_DAY_DURATION_MINUTES);

        buildPacket();
    }
}
