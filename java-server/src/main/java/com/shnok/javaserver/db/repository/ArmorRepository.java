package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DatabaseConfig;
import com.shnok.javaserver.db.entity.DBArmor;
import com.shnok.javaserver.db.interfaces.ArmorDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.util.List;

@Log4j2
public class ArmorRepository implements ArmorDao {
    @Override
    public DBArmor getArmorById(int id) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.get(DBArmor.class, id);
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DBArmor> getAllArmors() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.createQuery("SELECT a FROM DBArmor a", DBArmor.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
