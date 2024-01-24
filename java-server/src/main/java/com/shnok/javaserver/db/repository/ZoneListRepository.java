package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DatabaseConfig;
import com.shnok.javaserver.db.entity.ZoneList;
import com.shnok.javaserver.db.interfaces.ZoneListDao;
import javolution.util.FastMap;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
public class ZoneListRepository implements ZoneListDao {
    @Override
    public List<ZoneList> getAllZoneList() {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.createQuery("select s from ZoneList s", ZoneList.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    public Map<String, ZoneList> getAllZoneMap() {
        List<ZoneList> zoneLists = getAllZoneList();
        Map<String, ZoneList> zoneMap = new FastMap<>();
        for(ZoneList z : zoneLists) {
            zoneMap.put(z.getId(), z);
        }
        return zoneMap;
    }

    @Override
    public ZoneList getZoneListById(String id) {
        try (Session session = DatabaseConfig.getSessionFactory().openSession()) {
            return session.get(ZoneList.class, id);
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
