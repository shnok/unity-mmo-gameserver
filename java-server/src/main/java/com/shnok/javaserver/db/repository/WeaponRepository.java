package com.shnok.javaserver.db.repository;

import com.shnok.javaserver.db.DbFactory;
import com.shnok.javaserver.db.entity.DBWeapon;
import com.shnok.javaserver.db.interfaces.WeaponDao;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Session;

import java.util.List;

@Log4j2
public class WeaponRepository implements WeaponDao {
    @Override
    public DBWeapon getWeaponById(int id) {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            return session.get(DBWeapon.class, id);
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public List<DBWeapon> getAllWeapons() {
        try (Session session = DbFactory.getSessionFactory().openSession()) {
            return session.createQuery("SELECT w FROM DBWeapon w", DBWeapon.class)
                    .getResultList();
        } catch (Exception e) {
            log.error("SQL ERROR: {}", e.getMessage(), e);
            return null;
        }
    }
}
