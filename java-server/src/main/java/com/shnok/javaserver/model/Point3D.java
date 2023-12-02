package com.shnok.javaserver.model;

import java.io.Serializable;

public class Point3D implements Serializable {
    private static final long serialVersionUID = 4638345252031872576L;

    private volatile float x, y, z;

    public Point3D() {
    }

    public Point3D(float pX, float pY, float pZ) {
        x = pX;
        y = pY;
        z = pZ;
    }

    public Point3D(int pX, int pY, int pZ) {
        x = (float) pX;
        y = (float) pY;
        z = (float) pZ;
    }

    public Point3D(float pX, float pY) {
        x = pX;
        y = pY;
        z = 0;
    }

    public Point3D(Point3D worldPosition) {
        synchronized (worldPosition) {
            x = worldPosition.x;
            y = worldPosition.y;
            z = worldPosition.z;
        }
    }

    public static float distanceSquared(Point3D point1, Point3D point2) {
        float dx, dy;
        synchronized (point1) {
            synchronized (point2) {
                dx = point1.x - point2.x;
                dy = point1.y - point2.y;
            }
        }
        return (dx * dx) + (dy * dy);
    }

    public static boolean distanceLessThan(Point3D point1, Point3D point2, double distance) {
        return distanceSquared(point1, point2) < (distance * distance);
    }

    /*@Override
    public int hashCode() {
        return x ^ y ^ z;
    }*/

    public synchronized void setTo(Point3D point) {
        synchronized (point) {
            x = point.x;
            y = point.y;
            z = point.z;
        }
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    @Override
    public synchronized boolean equals(Object o) {
        if (o instanceof Point3D) {
            Point3D point3D = (Point3D) o;
            boolean ret;
            synchronized (point3D) {
                ret = (point3D.x == x) && (point3D.y == y) && (point3D.z == z);
            }
            return ret;
        }
        return false;
    }

    public synchronized boolean equals(float pX, float pY, float pZ) {
        return (x == pX) && (y == pY) && (z == pZ);
    }

    public synchronized float distanceSquaredTo(Point3D point) {
        float dx, dy;
        synchronized (point) {
            dx = x - point.x;
            dy = y - point.y;
        }
        return (dx * dx) + (dy * dy);
    }

    public float getX() {
        return x;
    }

    public synchronized void setX(float pX) {
        x = pX;
    }

    public float getY() {
        return y;
    }

    public synchronized void setY(float pY) {
        y = pY;
    }

    public float getZ() {
        return z;
    }

    public synchronized void setZ(float pZ) {
        z = pZ;
    }

    public synchronized void setXYZ(float pX, float pY, float pZ) {
        x = pX;
        y = pY;
        z = pZ;
    }
}