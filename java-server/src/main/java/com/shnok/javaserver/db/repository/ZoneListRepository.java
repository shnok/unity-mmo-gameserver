package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.db.DatabaseConfig;
import com.shnok.javaserver.db.entity.ZoneList;
import com.shnok.javaserver.db.interfaces.ZoneListDao;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.util.VectorUtils;
import javolution.util.FastMap;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

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
        log.debug("Loaded {} zone info(s).", zoneLists.size());
        Map<String, ZoneList> zoneMap = new FastMap<>();
        for(ZoneList zone : zoneLists) {
            Point3D origin = VectorUtils.floorToNearest(zone.getOrigin(), Config.NODE_SIZE);
            zone.setOrigX(origin.getX());
            zone.setOrigY(origin.getY());
            zone.setOrigZ(origin.getZ());

            zoneMap.put(zone.getId(), zone);
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