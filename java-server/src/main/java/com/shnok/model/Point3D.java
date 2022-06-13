package com.shnok.model;

import java.io.Serializable;

public class Point3D implements Serializable {
    private static final long serialVersionUID = 4638345252031872576L;

    private volatile float _x, _y, _z;

    public Point3D() {}
    public Point3D(float pX, float pY, float pZ) {
        _x = pX;
        _y = pY;
        _z = pZ;
    }

    public Point3D(float pX, float pY) {
        _x = pX;
        _y = pY;
        _z = 0;
    }

    public Point3D(Point3D worldPosition) {
        synchronized (worldPosition) {
            _x = worldPosition._x;
            _y = worldPosition._y;
            _z = worldPosition._z;
        }
    }

    public synchronized void setTo(Point3D point) {
        synchronized (point) {
            _x = point._x;
            _y = point._y;
            _z = point._z;
        }
    }

    @Override
    public String toString() {
        return "(" + _x + ", " + _y + ", " + _z + ")";
    }

    /*@Override
    public int hashCode() {
        return _x ^ _y ^ _z;
    }*/

    @Override
    public synchronized boolean equals(Object o) {
        if (o instanceof Point3D) {
            Point3D point3D = (Point3D) o;
            boolean ret;
            synchronized (point3D) {
                ret = (point3D._x == _x) && (point3D._y == _y) && (point3D._z == _z);
            }
            return ret;
        }
        return false;
    }

    public synchronized boolean equals(float pX, float pY, float pZ) {
        return (_x == pX) && (_y == pY) && (_z == pZ);
    }

    public synchronized float distanceSquaredTo(Point3D point) {
        float dx, dy;
        synchronized (point) {
            dx = _x - point._x;
            dy = _y - point._y;
        }
        return (dx * dx) + (dy * dy);
    }

    public static float distanceSquared(Point3D point1, Point3D point2) {
        float dx, dy;
        synchronized (point1) {
            synchronized (point2) {
                dx = point1._x - point2._x;
                dy = point1._y - point2._y;
            }
        }
        return (dx * dx) + (dy * dy);
    }

    public static boolean distanceLessThan(Point3D point1, Point3D point2, double distance) {
        return distanceSquared(point1, point2) < (distance * distance);
    }

    public float getX() {
        return _x;
    }

    public synchronized void setX(float pX) {
        _x = pX;
    }

    public float getY() {
        return _y;
    }

    public synchronized void setY(float pY) {
        _y = pY;
    }

    public float getZ() {
        return _z;
    }

    public synchronized void setZ(float pZ) {
        _z = pZ;
    }

    public synchronized void setXYZ(float pX, float pY, float pZ) {
        _x = pX;
        _y = pY;
        _z = pZ;
    }
}