package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DatabaseConfig;
import com.shnok.javaserver.db.entity.DBPlayerItem;
import com.shnok.javaserver.db.interfaces.PlayerItemDao;
import com.shnok.javaserver.enums.ItemLocation;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.util.List;

@Log4j2
public class PlayerItemRepository implements PlayerItemDao {
    @Override
    public List<DBPlayerItem> getAllItemsForUser(int id) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.createQuery("SELECT i FROM DBPlayerItem i WHERE owner_id=" + id, DBPlayerItem.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DBPlayerItem> getEquippedItemsForUser(int id) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.createQuery("SELECT i FROM DBPlayerItem i WHERE loc=" + ItemLocation.EQUIPPED.getValue() +
                            " AND owner_id=" + id, DBPlayerItem.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DBPlayerItem> getInventoryItemsForUser(int id) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.createQuery("SELECT i FROM DBPlayerItem i WHERE loc=" + ItemLocation.INVENTORY.getValue() +
                            " AND owner_id=" + id, DBPlayerItem.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DBPlayerItem> getWarehouseItemsForUser(int id) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.createQuery("SELECT i FROM DBPlayerItem i WHERE loc=" + ItemLocation.WAREHOUSE.getValue() +
                            " AND owner_id=" + id, DBPlayerItem.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
