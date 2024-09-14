package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.db.entity.DBItem;
import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.SystemMessageId;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.NpcInstance;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.template.NpcTemplate;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

@Log4j2
public class SystemMessagePacket extends SendablePacket {
    private static final SMParam[] EMPTY_PARAM_ARRAY = new SMParam[0];

    private static final class SMParam {
        private final byte _type;
        private final Object _value;

        public SMParam(final byte type, final Object value) {
            _type = type;
            _value = value;
        }

        public byte getType() {
            return _type;
        }

        public Object getValue() {
            return _value;
        }

        public String getStringValue() {
            return (String) _value;
        }

        public int getIntValue() {
            return (Integer) _value;
        }

        public long getLongValue() {
            return (Long) _value;
        }

        public int[] getIntArrayValue() {
            return (int[]) _value;
        }

        public float[] getFloatArrayValue() {
            return (float[]) _value;
        }
    }

    // 15 exists in goddess of destruction but also may works in h5 needs to be verified!
    // private static final byte TYPE_CLASS_ID = 15;
    // id 14 unknown
    private static final byte TYPE_SYSTEM_STRING = 13;
    private static final byte TYPE_PLAYER_NAME = 12;
    private static final byte TYPE_DOOR_NAME = 11;
    private static final byte TYPE_INSTANCE_NAME = 10;
    private static final byte TYPE_ELEMENT_NAME = 9;
    // id 8 - same as 3
    private static final byte TYPE_ZONE_NAME = 7;
    private static final byte TYPE_LONG_NUMBER = 6;
    private static final byte TYPE_CASTLE_NAME = 5;
    private static final byte TYPE_SKILL_NAME = 4;
    private static final byte TYPE_ITEM_NAME = 3;
    private static final byte TYPE_NPC_NAME = 2;
    private static final byte TYPE_INT_NUMBER = 1;
    private static final byte TYPE_TEXT = 0;

    private SMParam[] _params;
    private final SystemMessageId _smId;
    private int _paramIndex;

//    public SystemMessagePacket(SystemMessageId type) {
//        super(ServerPacketType.SystemMessage.getValue());
//        this.type = type;
//
//        writeB((byte) type.ordinal());
//        writeS(value);
//
//        buildPacket();
//    }

    public SystemMessagePacket(SystemMessageId smId) {
        super(ServerPacketType.SystemMessage.getValue());
        
        if (smId == null) {
            throw new NullPointerException("SystemMessageId cannot be null!");
        }
        
        _smId = smId;

        if(smId.getParams() > 0) {
            _params = new SMParam[smId.getParams()];
        } else {
            _params = EMPTY_PARAM_ARRAY;
            writeMe();
        }
    }

    public final int getId() {
        return _smId.getId();
    }

    public final SystemMessageId getSystemMessageId() {
        return _smId;
    }

    private void append(SMParam param) {
        if (_paramIndex >= _params.length) {
            _params = Arrays.copyOf(_params, _paramIndex + 1);
            _smId.setParamCount(_paramIndex + 1);
            log.info("Wrong parameter count '" + (_paramIndex + 1) + "' for SystemMessageId: " + _smId);
        }

        _params[_paramIndex++] = param;
    }

    public void addString(final String text) {
        append(new SMParam(TYPE_TEXT, text));
    }

    public void addInt(final int number) {
        append(new SMParam(TYPE_INT_NUMBER, number));
    }

    public void addLong(final long number) {
        append(new SMParam(TYPE_LONG_NUMBER, number));
    }

    public void addCharName(final Entity cha) {
        if (cha.isNpc()) {
            final NpcInstance npc = (NpcInstance) cha;
            addString(npc.getTemplate().getName());
            return;
        } else if (cha.isPlayer()) {
            addPcName((PlayerInstance) cha);
            return;
        }

        addString("Unknown");
    }

    public void addPcName(final PlayerInstance pc) {
        append(new SMParam(TYPE_PLAYER_NAME, pc.getName()));
    }

    public void addNpcName(NpcInstance npc) {
        addNpcName(npc.getTemplate());
    }

    public void addNpcName(final NpcTemplate template) {
        addNpcName(template.getNpcId());
    }

    public void addNpcName(final int id) {
        append(new SMParam(TYPE_NPC_NAME, 1000000 + id));
    }

    public void addItemName(final ItemInstance item) {
        addItemName(item.getItemId());
    }

    public void addItemName(final DBItem item) {
        addItemName(item.getId());
    }

    public void addItemName(final int id) {
        append(new SMParam(TYPE_ITEM_NAME, id));
    }

    public void addZoneName(final int x, final int y, final int z) {
        append(new SMParam(TYPE_ZONE_NAME, new int[] {
                x,
                y,
                z
        }));
    }

    public void addSkillName(final Skill skill) {
        addString(skill.getName());
    }

    public void addSkillName(final int id) {
        addSkillName(id, 1);
    }

    public void addSkillName(final int id, final int lvl) {
        append(new SMParam(TYPE_SKILL_NAME, new int[] {
                id,
                lvl
        }));
    }


    public void addElemental(final int type) {
        append(new SMParam(TYPE_ELEMENT_NAME, type));
    }

    /**
     * ID from sysstring-e.dat
     * @param type
     * @return
     */
    public void addSystemString(final int type) {
        append(new SMParam(TYPE_SYSTEM_STRING, type));
    }

    /**
     * Instance name from instantzonedata-e.dat
     * @param type id of instance
     * @return
     */
    public void addInstanceName(final int type) {
        append(new SMParam(TYPE_INSTANCE_NAME, type));
    }

    public final void writeMe() {
        writeI(getId());
        writeB((byte) _params.length);

        SMParam param;
        for (int i = 0; i < _paramIndex; i++) {
            param = _params[i];

            writeB(param.getType());

            switch (param.getType()) {
                case TYPE_TEXT:
                case TYPE_PLAYER_NAME:
                    writeS(param.getStringValue());
                    break;
                case TYPE_LONG_NUMBER:
                    writeL(param.getLongValue());
                    break;
                case TYPE_ITEM_NAME:
                case TYPE_CASTLE_NAME:
                case TYPE_INT_NUMBER:
                case TYPE_NPC_NAME:
                case TYPE_ELEMENT_NAME:
                case TYPE_SYSTEM_STRING:
                case TYPE_INSTANCE_NAME:
                case TYPE_DOOR_NAME:
                    writeI(param.getIntValue());
                    break;
                case TYPE_SKILL_NAME:
                    final int[] array = param.getIntArrayValue();
                    writeI(array[0]); // SkillId
                    writeI(array[1]); // SkillLevel
                    break;
                case TYPE_ZONE_NAME:
                    final float[] array2 = param.getFloatArrayValue();
                    writeF(array2[0]); // x
                    writeF(array2[1]); // y
                    writeF(array2[2]); // z
                    break;
            }
        }

        buildPacket();
    }


    public static SystemMessagePacket sendString(final String text) {
        if (text == null) {
            throw new NullPointerException();
        }

        final SystemMessagePacket sm = SystemMessagePacket.getSystemMessage(SystemMessageId.S1);
        sm.addString(text);
        sm.writeMe();
        return sm;
    }

    public static SystemMessagePacket getSystemMessage(final SystemMessageId smId) {
        SystemMessagePacket sm = smId.getStaticSystemMessage();
        if (sm != null) {
            return sm;
        }

        sm = new SystemMessagePacket(smId);
        if (smId.getParams() == 0) {
            smId.setStaticSystemMessage(sm);
        }

        return sm;
    }
}
