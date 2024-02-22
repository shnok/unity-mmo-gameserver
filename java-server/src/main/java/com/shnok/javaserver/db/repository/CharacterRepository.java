package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DatabaseConfig;
import com.shnok.javaserver.db.entity.Character;
import com.shnok.javaserver.db.entity.Item;
import com.shnok.javaserver.db.interfaces.CharacterDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

@Log4j2
public class CharacterRepository implements CharacterDao {
    @Override
    public Character getRandomCharacter() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.createQuery("SELECT c FROM CHARACTER c ORDER BY RAND() LIMIT 1", Character.class)
                    .getSingleResult();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
