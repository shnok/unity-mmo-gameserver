package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.object.GameObject;

import java.util.ArrayList;

public class StatusUpdatePacket extends SendablePacket {
    public static final int LEVEL = 0x01;
    public static final int EXP = 0x02;
    public static final int STR = 0x03;
    public static final int DEX = 0x04;
    public static final int CON = 0x05;
    public static final int INT = 0x06;
    public static final int WIT = 0x07;
    public static final int MEN = 0x08;

    public static final int CUR_HP = 0x09;
    public static final int MAX_HP = 0x0a;
    public static final int CUR_MP = 0x0b;
    public static final int MAX_MP = 0x0c;

    public static final int SP = 0x0d;
    public static final int CUR_LOAD = 0x0e;
    public static final int MAX_LOAD = 0x0f;

    public static final int P_ATK = 0x11;
    public static final int ATK_SPD = 0x12;
    public static final int P_DEF = 0x13;
    public static final int EVASION = 0x14;
    public static final int ACCURACY = 0x15;
    public static final int CRITICAL = 0x16;
    public static final int M_ATK = 0x17;
    public static final int CAST_SPD = 0x18;
    public static final int M_DEF = 0x19;
    public static final int PVP_FLAG = 0x1a;
    public static final int KARMA = 0x1b;

    public static final int CUR_CP = 0x21;
    public static final int MAX_CP = 0x22;

    private final int objectId;
    private final ArrayList<Attribute> attributes = new ArrayList<>();

    static class Attribute {
        /**
         * id values 09 - current health 0a - max health 0b - current mana 0c - max mana
         */
        public int id;
        public int value;

        Attribute(int pId, int pValue) {
            id = pId;
            value = pValue;
        }
    }

    public StatusUpdatePacket(int objectId) {
        super(ServerPacketType.StatusUpdate.getValue());
        this.objectId = objectId;
    }

    public StatusUpdatePacket(GameObject object) {
        super(ServerPacketType.StatusUpdate.getValue());
        this.objectId = object.getId();
    }

    public void build() {
        writeI(objectId);
        writeB((byte) attributes.size());

        for (Attribute attribute : attributes) {
            writeB((byte) attribute.id);
            writeI(attribute.value);
        }

        buildPacket();
    }

    public void addAttribute(int id, int level) {
        attributes.add(new Attribute(id, level));
    }

    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }
}
