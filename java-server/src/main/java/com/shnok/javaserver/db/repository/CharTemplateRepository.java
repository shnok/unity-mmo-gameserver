package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DatabaseConfig;
import com.shnok.javaserver.db.entity.DBCharTemplate;
import com.shnok.javaserver.db.interfaces.CharTemplateDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

@Log4j2
public class CharTemplateRepository implements CharTemplateDao {
    @Override
    public DBCharTemplate getTemplateByClassId(int id) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.get(DBCharTemplate.class, id);
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
