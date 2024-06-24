package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DbFactory;
import com.shnok.javaserver.db.entity.DBZoneList;
import com.shnok.javaserver.db.interfaces.ZoneListDao;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.util.VectorUtils;
import javolution.util.FastMap;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.util.List;
import java.util.Map;

import static com.shnok.javaserver.config.Configuration.serverConfig;

@Log4j2
public class ZoneListRepository implements ZoneListDao {
    @Override
    public List<DBZoneList> getAllZoneList() {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            return session.createQuery("select s from DBZoneList s", DBZoneList.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    public Map<String, DBZoneList> getAllZoneMap() {
        List<DBZoneList> zoneLists = getAllZoneList();
        log.debug("Loaded {} zone info(s).", zoneLists.size());
        Map<String, DBZoneList> zoneMap = new FastMap<>();
        for(DBZoneList zone : zoneLists) {
            Point3D origin = VectorUtils.floorToNearest(zone.getOrigin(), serverConfig.geodataNodeSize());
            zone.setOrigX(origin.getX());
            zone.setOrigY(origin.getY());
            zone.setOrigZ(origin.getZ());

            zoneMap.put(zone.getId(), zone);
        }
        return zoneMap;
    }

    @Override
    public DBZoneList getZoneListById(String id) {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            return session.get(DBZoneList.class, id);
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
