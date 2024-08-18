package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DbFactory;
import com.shnok.javaserver.db.entity.DBPlayerItem;
import com.shnok.javaserver.db.interfaces.PlayerItemDao;
import com.shnok.javaserver.enums.ItemLocation;
import com.shnok.javaserver.enums.item.ItemSlot;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.util.List;
import java.util.Random;

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

    @Override
    public int savePlayerItem(DBPlayerItem playerItem) {
        int id = 0;
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            session.beginTransaction();
            id = (int) session.save(playerItem);
            session.getTransaction().commit();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
        }

        return id;
    }

    public void giveRandomGearToCharacter(int owner) {
        int[] rhand = new int[] { 6, 2370, 89, 5284, 177 };
        int shieldId = 20;
        int[] legs = new int[] { 1147, 461 };
        int[] chest = new int[] { 1146, 425 };

        Random random = new Random();

        int rhandId = rhand[random.nextInt(rhand.length)];
        DBPlayerItem rhandItem = new DBPlayerItem(owner, rhandId, ItemLocation.EQUIPPED, ItemSlot.rhand);
        savePlayerItem(rhandItem);

        if(rhandId != 5284 && rhandId != 177) {
            if(random.nextInt(3) == 1) {
                DBPlayerItem lhandItem = new DBPlayerItem(owner, shieldId, ItemLocation.EQUIPPED, ItemSlot.lhand);
                savePlayerItem(lhandItem);
            }
        }

        int legsId = legs[random.nextInt(legs.length)];
        DBPlayerItem legsItem = new DBPlayerItem(owner, legsId, ItemLocation.EQUIPPED, ItemSlot.legs);
        savePlayerItem(legsItem);

        int chestId = chest[random.nextInt(chest.length)];
        DBPlayerItem chestItem = new DBPlayerItem(owner, chestId, ItemLocation.EQUIPPED, ItemSlot.chest);
        savePlayerItem(chestItem);
    }

    public void giveRandomItemsToPlayer(int owner) {
        Random random = new Random();

        DBPlayerItem adena = new DBPlayerItem(owner, 57, ItemLocation.INVENTORY, 0, random.nextInt(1000000));
        savePlayerItem(adena);

        DBPlayerItem item2 = new DBPlayerItem(owner, 1835, ItemLocation.INVENTORY, 2, random.nextInt(1000));
        savePlayerItem(item2);
        DBPlayerItem item3 = new DBPlayerItem(owner, 3947, ItemLocation.INVENTORY, 3, random.nextInt(1000));
        savePlayerItem(item3);
        DBPlayerItem item4 = new DBPlayerItem(owner, 2509, ItemLocation.INVENTORY, 10, random.nextInt(1000));
        savePlayerItem(item4);
        DBPlayerItem item5 = new DBPlayerItem(owner, 736, ItemLocation.INVENTORY, 20, random.nextInt(1000));
        savePlayerItem(item5);

        DBPlayerItem weapon1 = new DBPlayerItem(owner, 2370, ItemLocation.INVENTORY, ItemSlot.lhand);
        savePlayerItem(weapon1);
    }
}
