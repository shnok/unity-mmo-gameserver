package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DbFactory;
import com.shnok.javaserver.db.entity.DBEtcItem;
import com.shnok.javaserver.db.interfaces.EtcItemDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.util.List;

@Log4j2
public class EtcItemRepository implements EtcItemDao {
    @Override
    public DBEtcItem getEtcItemById(int id) {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            return session.get(DBEtcItem.class, id);
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DBEtcItem> getAllEtcItems() {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            return session.createQuery("SELECT e FROM DBEtcItem e", DBEtcItem.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
