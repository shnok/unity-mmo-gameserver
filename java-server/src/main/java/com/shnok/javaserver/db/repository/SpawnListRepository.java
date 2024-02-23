package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DatabaseConfig;
import com.shnok.javaserver.db.entity.DBSpawnList;
import com.shnok.javaserver.db.interfaces.SpawnListDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.util.List;

@Log4j2
public class SpawnListRepository implements SpawnListDao {
    public SpawnListRepository() {}

    @Override
    public void addSpawnList(DBSpawnList spawnList) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(spawnList);
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
        }
    }

    @Override
    public DBSpawnList getSpawnListById(int id) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.get(DBSpawnList.class, id);
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DBSpawnList> getAllSpawnList() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.createQuery("select s from DBSpawnList s", DBSpawnList.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DBSpawnList> getAllMonsters() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.createQuery("SELECT s FROM DBSpawnList s \n" +
                            "INNER JOIN\n" +
                            "DBNpc n ON \n" +
                            "s.npcId = n.idTemplate\n" +
                            "AND n.type = 'L2Monster'", DBSpawnList.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DBSpawnList> getAllNPCs() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.createQuery("SELECT s FROM DBSpawnList s \n" +
                            "INNER JOIN\n" +
                            "DBNpc n ON \n" +
                            "s.npcId = n.idTemplate\n" +
                            "AND n.type <> 'L2Monster'", DBSpawnList.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
