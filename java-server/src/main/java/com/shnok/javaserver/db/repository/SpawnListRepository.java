package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DatabaseConfig;
import com.shnok.javaserver.db.entity.SpawnList;
import com.shnok.javaserver.db.interfaces.SpawnListDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.util.List;

@Log4j2
public class SpawnListRepository implements SpawnListDao {
    public SpawnListRepository() {}

    @Override
    public void addSpawnList(SpawnList spawnList) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.save(spawnList);
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
        }
    }

    @Override
    public SpawnList getSpawnListById(int id) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.get(SpawnList.class, id);
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<SpawnList> getAllSpawnList() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.createQuery("select s from SpawnList s", SpawnList.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<SpawnList> getAllMonsters() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.createQuery("SELECT s FROM SpawnList s \n" +
                            "INNER JOIN\n" +
                            "Npc n ON \n" +
                            "s.npcId = n.idTemplate\n" +
                            "AND n.type = 'L2Monster'", SpawnList.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<SpawnList> getAllNPCs() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.createQuery("SELECT s FROM SpawnList s \n" +
                            "INNER JOIN\n" +
                            "Npc n ON \n" +
                            "s.npcId = n.idTemplate\n" +
                            "AND n.type <> 'L2Monster'", SpawnList.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
