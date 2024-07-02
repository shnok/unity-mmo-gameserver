package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.db.entity.DBCharacter;
import com.shnok.javaserver.db.repository.CharacterRepository;
import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.ClassId;
import com.shnok.javaserver.model.CharSelectInfoPackage;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static com.shnok.javaserver.dto.external.serverpackets.PlayerInfoPacket.PAPERDOLL_ORDER;

@Getter
public class CharSelectionInfoPacket extends SendablePacket {
    List<CharSelectInfoPackage> charSelectData;
    public CharSelectionInfoPacket(String account, int playOkID1) {
        super(ServerPacketType.CharSelectionInfo.getValue());

        charSelectData = loadCharacterSelectInfo(account);

        writeB((byte) charSelectData.size());

        writeB((byte) 0x07); //maximum character slots

        int autoSelectedCharacterId = -1;
        long lastAccess = -1L;
        for (final CharSelectInfoPackage charInfoPackage : charSelectData) {
            if (charInfoPackage.getDeleteTimer() == 0L) {
                if (lastAccess < charInfoPackage.getLastAccess()) {
                    lastAccess = charInfoPackage.getLastAccess();
                    autoSelectedCharacterId = charInfoPackage.getObjectId();
                }
            }
        }

        for (final CharSelectInfoPackage character : charSelectData) {
            writeS(character.getName());
            writeI(character.getObjectId());
            writeS(account);
            writeI(character.getClanId());

            writeB((byte) character.getSex());
            writeB((byte) character.getRace());
            writeB(ClassId.getById((byte) character.getClassId()).isMage() ? (byte) 0x01 : (byte) 0x00);
            writeB((byte) character.getClassId());

            writeF(character.getX());
            writeF(character.getY());
            writeF(character.getZ());

            writeI(character.getCurrentHp());
            writeI(character.getCurrentMp());

            writeI(character.getSp());
            writeI(character.getExp());
            writeF(0); // exp % //TODO update exp %

            writeI(character.getLevel());

            writeI(character.getKarma());
            writeI(character.getPkKills());
            writeI(character.getPvpKills());

            for (byte slot : PAPERDOLL_ORDER) {
                writeI(character.getPaperdollItemId(slot));
            }

            writeB((byte) character.getHairStyle());
            writeB((byte) character.getHairColor());
            writeB((byte) character.getFace());

            writeI(character.getMaxHp()); // hp max
            writeI(character.getMaxMp()); // mp max

            writeI(character.getDeleteTimer() > 0 ?
                    (int) ((character.getDeleteTimer() - System.currentTimeMillis()) / 1000) : 0); //remaining before delete
            writeB(character.getObjectId() == autoSelectedCharacterId ? (byte) 0x01 : 0); //auto select ?
        }

        buildPacket();
    }

    private static List<CharSelectInfoPackage> loadCharacterSelectInfo(String account) {
        List<DBCharacter> characters = CharacterRepository.getInstance().getCharactersForAccount(account);

        List<CharSelectInfoPackage> characterList = new ArrayList<>();

        characters.forEach((character -> {
            CharSelectInfoPackage charInfo = parseCharacter(character);

            if (charInfo != null) {
                characterList.add(charInfo);
            }
        }));

        return characterList;
    }

    private static CharSelectInfoPackage parseCharacter(DBCharacter character) {
        int objectId = character.getId();
        String name = character.getCharName();

        // See if the char must be deleted
        Long deleteTime = character.getDelete_time();
        if (deleteTime != null && deleteTime > 0) {
            if (System.currentTimeMillis() > deleteTime) {
                //TODO: Delete char and remove from clan
                return null;
            }
        }

        CharSelectInfoPackage charInfo = new CharSelectInfoPackage(objectId, name);

        charInfo.setAccessLevel(character.getAccessLevel());
        charInfo.setLevel(character.getLevel());
        charInfo.setMaxCp(character.getMaxCp());
        charInfo.setCurrentCp(character.getCurCp());
        charInfo.setMaxHp(character.getMaxHp());
        charInfo.setCurrentHp(character.getMaxHp());
        charInfo.setMaxMp(character.getMaxMp());
        charInfo.setCurrentMp(character.getCurMp());
        charInfo.setKarma(character.getKarma());
        charInfo.setPkKills(character.getPkKills());
        charInfo.setPvpKills(character.getPvpKills());
        charInfo.setFace(character.getFace());
        charInfo.setHairStyle(character.getHairStyle());
        charInfo.setHairColor(character.getHairColor());
        charInfo.setSex(character.getSex());

        charInfo.setExp(character.getExp());
        charInfo.setSp(character.getSp());

        if(character.getClan_id() == null) {
            charInfo.setClanId(-1);
        } else {
            charInfo.setClanId(character.getClan_id());
        }
        charInfo.setRace(character.getRace().getValue());

        charInfo.setX(character.getPosX());
        charInfo.setY(character.getPosY());
        charInfo.setZ(character.getPosZ());

        charInfo.setClassId(character.getClassId().getId());

        if(deleteTime == null) {
            charInfo.setDeleteTimer(0);
        } else {
            charInfo.setDeleteTimer(deleteTime);
        }

        charInfo.setLastAccess(character.getLastLogin());
        return charInfo;
    }
}
