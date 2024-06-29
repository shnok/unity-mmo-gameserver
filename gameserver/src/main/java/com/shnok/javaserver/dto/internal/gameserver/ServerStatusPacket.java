package com.shnok.javaserver.dto.internal.gameserver;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.internal.GameServerPacketType;

import java.util.ArrayList;

public class ServerStatusPacket extends SendablePacket {
    private final ArrayList<Attribute> attributes;

    // Attributes
    public static final int SERVER_LIST_STATUS = 0x00;
    public static final int MAX_PLAYERS = 0x01;

    // Server Status
    public static final int STATUS_LIGHT = 0x00;
    public static final int STATUS_NORMAL = 0x01;
    public static final int STATUS_HEAVY = 0x02;
    public static final int STATUS_FULL = 0x03;
    public static final int STATUS_DOWN = 0x04;
    public static final int STATUS_GM_ONLY = 0x05;

    public static final String[] STATUS_STRING = {
            "Light",
            "Normal",
            "Heavy",
            "Full",
            "Down",
            "Gm Only"
    };

    public ServerStatusPacket() {
        super(GameServerPacketType.ServerStatus.getValue());
        attributes = new ArrayList<>();
    }

    public void build() {
        writeB((byte) 0);
        writeB((byte) 0);
        writeI(attributes.size());
        for (Attribute temp : attributes) {
            writeI(temp.id);
            writeI(temp.value);
        }

        buildPacket();
    }

    public void addAttribute(int id, int value) {
        attributes.add(new Attribute(id, value));
    }

    static class Attribute {
        public int id;
        public int value;

        Attribute(int pId, int pValue) {
            id = pId;
            value = pValue;
        }
    }
}