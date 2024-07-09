package com.shnok.javaserver.util;

import com.shnok.javaserver.model.object.GameObject;
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

    public static Point3D flatten(Point3D position) {
        return new Point3D(position.getX(), 0, position.getZ());
    }

    public static float calcDistance(Point3D from, Point3D to) {
        float dx = (to.getX() - from.getX());
        float dy = (to.getY() - from.getY());
        float dz = (to.getZ() - from.getZ());
        return (float) Math.sqrt((dx * dx) + (dz * dz) + (dy * dy));
    }

    public static float calcDistance2D(Point3D from, Point3D to) {
        float dx = (to.getX() - from.getX());
        float dz = (to.getZ() - from.getZ());
        return (float) Math.sqrt((dx * dx) + (dz * dz));
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

    public static Point3D lerpPosition(Point3D start, Point3D end, float percentage) {
        float deltaX = end.getX() - start.getX();
        float deltaY = end.getY() - start.getY();
        float deltaZ = end.getZ() - start.getZ();

        float scaledDeltaX = percentage * deltaX;
        float scaledDeltaY = percentage * deltaY;
        float scaledDeltaZ = percentage * deltaZ;

        float intermediateX = start.getX() + scaledDeltaX;
        float intermediateY = start.getY() + scaledDeltaY;
        float intermediateZ = start.getZ() + scaledDeltaZ;

        return new Point3D(intermediateX, intermediateY, intermediateZ);
    }

    public static float calculateMoveDirectionAngle(Point3D from, Point3D to) {
        // Calculate the direction vector (destination - current position)
        float directionX = to.getX() - from.getX();
        float directionZ = to.getZ() - from.getZ();

        return calculateMoveDirectionAngle(directionX, directionZ);
    }

    public static float calculateMoveDirectionAngle(float directionX, float directionZ) {
        return (float) Math.toDegrees(Math.atan2(directionX, directionZ)) ;
    }
}
