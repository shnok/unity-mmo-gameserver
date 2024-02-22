package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DatabaseConfig;
import com.shnok.javaserver.db.entity.Item;
import com.shnok.javaserver.db.interfaces.ItemDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.util.List;

@Log4j2
public class ItemRepository implements ItemDao {
    @Override
    public List<Item> getEquippedItemsForUser(int ownerId) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.createQuery("SELECT i FROM ITEM i WHERE loc=1 AND owner_id=" + ownerId, Item.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
