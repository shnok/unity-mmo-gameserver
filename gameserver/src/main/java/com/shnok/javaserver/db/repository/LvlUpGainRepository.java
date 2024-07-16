package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DbFactory;
import com.shnok.javaserver.db.entity.DBLevelUpGain;
import com.shnok.javaserver.db.interfaces.ClassLevelDao;
import com.shnok.javaserver.enums.ClassId;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class LvlUpGainRepository implements ClassLevelDao {
    private final Map<Integer, DBLevelUpGain> levelUpGainLookUp;
    private static LvlUpGainRepository instance;
    public static LvlUpGainRepository getInstance() {
        if (instance == null) {
            instance = new LvlUpGainRepository();
        }
        return instance;
    }

    public LvlUpGainRepository() {
        levelUpGainLookUp = new HashMap<>();

        List<DBLevelUpGain> data = getAllLevelUpGainData();

        data.forEach((d) -> {
            levelUpGainLookUp.put(d.getClassId(), d);
        });
    }

    @Override
    public DBLevelUpGain getLevelUpGainByClassId(int classId) {
//        try (Session session = DbFactory.getSessionFactory().openSession()) {
//            return session.createQuery("SELECT c FROM DBLevelUpGain c WHERE ClassId = :class_id", DBLevelUpGain.class)
//                    .setParameter("class_id", classId)
//                    .getSingleResult();
//        } catch (Exception e) {
//            log.error("SQL ERROR: {}", e.getMessage(), e);
//            return null;
//        }
        return levelUpGainLookUp.get(classId);
    }

    @Override
    public List<DBLevelUpGain> getAllLevelUpGainData() {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            return session.createQuery("SELECT c FROM DBLevelUpGain c", DBLevelUpGain.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

}
