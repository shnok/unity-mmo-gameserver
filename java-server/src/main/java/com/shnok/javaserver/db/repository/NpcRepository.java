package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DatabaseConfig;
import com.shnok.javaserver.db.entity.Npc;
import com.shnok.javaserver.db.interfaces.NpcDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

@Log4j2
public class NpcRepository implements NpcDao {
    @Override
    public Npc getNpcById(int id) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.get(Npc.class, id);
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
