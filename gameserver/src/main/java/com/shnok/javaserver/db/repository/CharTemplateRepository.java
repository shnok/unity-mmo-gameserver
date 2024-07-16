package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DbFactory;
import com.shnok.javaserver.db.entity.DBCharTemplate;
import com.shnok.javaserver.db.interfaces.CharTemplateDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class CharTemplateRepository implements CharTemplateDao {
    private final Map<Integer, DBCharTemplate> charTemplateLookup;
    private static CharTemplateRepository instance;
    public static CharTemplateRepository getInstance() {
        if (instance == null) {
            instance = new CharTemplateRepository();
        }
        return instance;
    }

    public CharTemplateRepository() {
        charTemplateLookup = new HashMap<>();

        List<DBCharTemplate> data = getAllCharTemplates();

        data.forEach((d) -> {
            charTemplateLookup.put(d.getClassId(), d);
        });
    }

    @Override
    public List<DBCharTemplate> getAllCharTemplates() {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            return session.createQuery("SELECT e FROM DBCharTemplate e", DBCharTemplate.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public DBCharTemplate getTemplateByClassId(int id) {
        return charTemplateLookup.get(id);
    }
}
