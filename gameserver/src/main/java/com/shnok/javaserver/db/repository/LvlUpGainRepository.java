package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DbFactory;
import com.shnok.javaserver.db.entity.DBLevelUpGain;
import com.shnok.javaserver.db.interfaces.ClassLevelDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

@Log4j2
public class LvlUpGainRepository implements ClassLevelDao {
    @Override
    public DBLevelUpGain getCharacterByClassId(int classId) {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            return session.createQuery("SELECT c FROM DBLevelUpGain c WHERE ClassId = :class_id", DBLevelUpGain.class)
                    .setParameter("class_id", classId)
                    .getSingleResult();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
