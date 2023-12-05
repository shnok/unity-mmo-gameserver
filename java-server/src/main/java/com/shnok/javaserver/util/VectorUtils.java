package com.shnok.javaserver.util;

import com.shnok.javaserver.model.GameObject;
import com.shnok.javaserver.model.Point3D;

public class VectorUtils {

    public static boolean checkIfInRange(int range, GameObject obj1, GameObject obj2) {
        if ((obj1 == null) || (obj2 == null)) {
            return false;
        }
        if (range == -1) {
            return true;
        }

        return calcDistance(obj1.getPos(), obj2.getPos()) <= range;
    }

    public static float calcDistance(Point3D from, Point3D to) {
        double dx = (to.getX() - from.getX());
        double dy = (to.getY() - from.getY());
        double dz = (to.getZ() - from.getZ());
        return (float) Math.sqrt((dx * dx) + (dz * dz) + (dy * dy));
    }
}
