package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DbFactory;
import com.shnok.javaserver.db.entity.DBPlayerItem;
import com.shnok.javaserver.db.interfaces.PlayerItemDao;
import com.shnok.javaserver.enums.ItemLocation;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.util.List;

@Log4j2
public class PlayerItemRepository implements PlayerItemDao {
    private static PlayerItemRepository instance;
    public static PlayerItemRepository getInstance() {
        if (instance == null) {
            instance = new PlayerItemRepository();
        }
        return instance;
    }

    @Override
    public List<DBPlayerItem> getAllItemsForUser(int id) {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            return session.createQuery("SELECT i FROM DBPlayerItem i WHERE owner_id=:owner_id", DBPlayerItem.class)
                    .setParameter("owner_id", id)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DBPlayerItem> getEquippedItemsForUser(int id) {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            List<DBPlayerItem> items = session.createQuery("SELECT i FROM DBPlayerItem i WHERE loc=:loc AND owner_id=:owner_id",
                            DBPlayerItem.class)
                    .setParameter("loc", ItemLocation.EQUIPPED.getValue())
                    .setParameter("owner_id", id)
                    .getResultList();

            log.debug("Loaded {} equipped item(s) for player {}", items.size(), id);
            return items;
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DBPlayerItem> getInventoryItemsForUser(int id) {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            List<DBPlayerItem> items = session.createQuery("SELECT i FROM DBPlayerItem i WHERE loc=" + ItemLocation.INVENTORY.getValue() +
                            " AND owner_id=" + id, DBPlayerItem.class)
                    .getResultList();

            log.debug("Loaded {} item(s) player's {} inventory.", items.size(), id);
            return items;
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DBPlayerItem> getWarehouseItemsForUser(int id) {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            List<DBPlayerItem> items = session.createQuery("SELECT i FROM DBPlayerItem i WHERE loc=" + ItemLocation.WAREHOUSE.getValue() +
                            " AND owner_id=" + id, DBPlayerItem.class)
                    .getResultList();

            log.debug("Loaded {} item(s) player's {} warehouse.", items.size(), id);
            return items;
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
