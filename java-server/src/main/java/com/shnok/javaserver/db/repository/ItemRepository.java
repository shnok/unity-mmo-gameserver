package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DatabaseConfig;
import com.shnok.javaserver.db.entity.Item;
import com.shnok.javaserver.db.entity.SpawnList;
import com.shnok.javaserver.db.interfaces.ItemDao;
import org.hibernate.Session;

import java.util.List;

public class ItemRepository implements ItemDao {
    @Override
    public List<Item> getEquippedItemsForUser(int userId) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.createQuery("select i from ITEM i where ", SpawnList.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
