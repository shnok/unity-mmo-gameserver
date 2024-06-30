package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DbFactory;
import com.shnok.javaserver.db.entity.DBNpc;
import com.shnok.javaserver.db.interfaces.NpcDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

@Log4j2
public class NpcRepository implements NpcDao {
    @Override
    public DBNpc getNpcById(int id) {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            return session.get(DBNpc.class, id);
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
