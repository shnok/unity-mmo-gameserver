package com.shnok.javaserver.util;

import com.shnok.javaserver.model.GameObject;
import com.shnok.javaserver.model.Point3D;

import java.util.Random;

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

    public static Point3D randomPos(Point3D center, float range) {
        Random random = new Random();
        float randomX = random.nextFloat() * (range * 2) - range;
        float randomZ = random.nextFloat() * (range * 2) - range;

        return new Point3D(center.getX() + randomX, center.getY(), center.getZ() + randomZ);
    }

    public static float floorToNearest(float value, float step) {
        return (float) (step * Math.floor(value / step));
    }

    public static Point3D floorToNearest(Point3D vector, float step) {
        return new Point3D(floorToNearest(vector.getX(), step),
                floorToNearest(vector.getY(), step),
                floorToNearest(vector.getZ(), step));
    }
}
