package com.shnok.javaserver.db.entity;

import com.shnok.javaserver.model.Point3D;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "ZONELIST")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DBZoneList {

    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column(name = "ORIG_X")
    private float origX;
    @Column(name = "ORIG_Y")
    private float origY;
    @Column(name = "ORIG_Z")
    private float origZ;
    @Column(name = "SIZE")
    private float zoneSize;

    public Point3D getOrigin() {
        return new Point3D(origX, origY, origZ);
    }

    public boolean isInBounds(Point3D location) {
        if(location.getX() < origX) {
            return false;
        }
        if(location.getX() > origX + zoneSize) {
            return false;
        }
        if(location.getZ() < origZ) {
            return false;
        }
        if(location.getZ() > origZ + zoneSize) {
            return false;
        }

        return true;
    }
}
