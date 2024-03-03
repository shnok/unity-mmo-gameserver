package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DatabaseConfig;
import com.shnok.javaserver.db.entity.DBCharacter;
import com.shnok.javaserver.db.entity.DBNpc;
import com.shnok.javaserver.db.interfaces.CharacterDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

@Log4j2
public class CharacterRepository implements CharacterDao {
    @Override
    public DBCharacter getRandomCharacter() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
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
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.get(DBCharacter.class, id);
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
