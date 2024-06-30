package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DbFactory;
import com.shnok.javaserver.db.entity.DBCharTemplate;
import com.shnok.javaserver.db.entity.DBCharacter;
import com.shnok.javaserver.db.entity.DBLevelUpGain;
import com.shnok.javaserver.db.interfaces.CharacterDao;
import com.shnok.javaserver.enums.ClassId;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.util.List;
import java.util.Random;

@Log4j2
public class CharacterRepository implements CharacterDao {
    private static CharacterRepository instance;
    public static CharacterRepository getInstance() {
        if (instance == null) {
            instance = new CharacterRepository();
        }
        return instance;
    }

    @Override
    public DBCharacter getRandomCharacter() {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            return session.createQuery("SELECT c FROM DBCharacter c ORDER BY RAND()", DBCharacter.class)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public DBCharacter getCharacterById(int id) {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            return session.get(DBCharacter.class, id);
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DBCharacter> getCharactersForAccount(String account) {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            return session.createQuery("SELECT c FROM DBCharacter c WHERE accountName = :accountName", DBCharacter.class)
                    .setParameter("accountName", account)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void saveOrUpdateCharacter(DBCharacter character) {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.saveOrUpdate(character);
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
        }
    }

    @Override
    public int saveCharacter(DBCharacter character) {
        int id = 0;
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            id = (int) session.save(character);
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
        }

        return id;
    }

    public void createRandomCharForAccount(String account) {
        log.info("Creating random character for account {}.", account);
        Random random = new Random();
        byte[] classes = {
                0x1f,
                0x26,
                0x35
        };

        DBCharTemplate charTemplate = CharTemplateRepository.getInstance().
                getTemplateByClassId(classes[random.nextInt(3)]);

        int face = random.nextInt(4);
        int hairStyle = random.nextInt(4);
        int hairColor = random.nextInt(4);

        float posX = 4724.32f;
        float posY = -68.00f;
        float posZ = -1731.24f;

        DBCharacter dbCharacter = new DBCharacter();
        dbCharacter.setTitle("");
        dbCharacter.setCharName(account);
        dbCharacter.setAccountName(account);
        dbCharacter.setAccessLevel(0);
        dbCharacter.setLevel(1);
        dbCharacter.setCharSlot((byte) 0);
        dbCharacter.setKarma(0);
        dbCharacter.setPkKills(0);
        dbCharacter.setPvpKills(0);
        dbCharacter.setMaxWeight(0);
        dbCharacter.setMaxWeight(10000);

        //STATS
        dbCharacter.setAcc(charTemplate.getAcc());
        dbCharacter.setCritical(charTemplate.getCritical());
        dbCharacter.setEvasion(charTemplate.getEvasion());
        dbCharacter.setMAtk(charTemplate.getMAtk());
        dbCharacter.setMDef(charTemplate.getMDef());
        dbCharacter.setMSpd(charTemplate.getMAtkSpd());
        dbCharacter.setPAtk(charTemplate.getPAtk());
        dbCharacter.setPDef(charTemplate.getPDef());
        dbCharacter.setPSpd(charTemplate.getPAtkSpd());
        dbCharacter.setWalkSpd(1);
        dbCharacter.setRunSpd(charTemplate.getMoveSpd());
        dbCharacter.setStr((byte) charTemplate.getStr());
        dbCharacter.setCon((byte) charTemplate.getCon());
        dbCharacter.setDex((byte) charTemplate.getDex());
        dbCharacter.set_int((byte) charTemplate.get_int());
        dbCharacter.setWit((byte) charTemplate.getWit());
        dbCharacter.setMen((byte) charTemplate.getMen());
        dbCharacter.setMen((byte) charTemplate.getMen());

        //HP MP CP
        DBLevelUpGain levelUpGain = LvlUpGainRepository.getInstance().
                getCharacterByClassId(charTemplate.getClassId());

        dbCharacter.setMaxHp((int) (levelUpGain.getDefaultHpBase() + levelUpGain.getDefaultHpAdd()));
        dbCharacter.setCurHp(dbCharacter.getMaxHp());
        dbCharacter.setMaxMp((int) (levelUpGain.getDefaultMpBase() + levelUpGain.getDefaultMpAdd()));
        dbCharacter.setMaxMp(dbCharacter.getMaxMp());
        dbCharacter.setMaxCp((int) (levelUpGain.getDefaultCpAdd() + levelUpGain.getDefaultCpAdd()));
        dbCharacter.setMaxCp(dbCharacter.getMaxCp());

        //Loc
        dbCharacter.setPosX(posX);
        dbCharacter.setPosY(posY);
        dbCharacter.setPosZ(posZ);
        dbCharacter.setHeading(0f);

        //Appearance
        dbCharacter.setClassId(ClassId.getById((byte) charTemplate.getClassId()));
        dbCharacter.setRace(charTemplate.getRace());
        dbCharacter.setFace((byte) face);
        dbCharacter.setHairColor((byte) hairColor);
        dbCharacter.setHairStyle((byte) hairStyle);
        dbCharacter.setSex((byte) 1);
        dbCharacter.setColR(charTemplate.getCollisionRadiusFemale());
        dbCharacter.setColH(charTemplate.getCollisionHeightFemale());

        int insertedId = saveCharacter(dbCharacter);

        PlayerItemRepository.getInstance().giveRandomGearToCharacter(insertedId);
    }
}
