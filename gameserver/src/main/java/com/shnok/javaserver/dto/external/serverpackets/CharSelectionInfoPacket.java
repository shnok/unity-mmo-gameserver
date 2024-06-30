package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.CharSelectInfoPackage;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import lombok.Getter;

import java.util.List;

@Getter
public class CharSelectionInfoPacket extends SendablePacket {
    List<CharSelectInfoPackage> charSelect;
    public CharSelectionInfoPacket(String account, int playOkID1) {
        super(ServerPacketType.CharSelectionInfo.getValue());
        buildPacket();
    }
}
