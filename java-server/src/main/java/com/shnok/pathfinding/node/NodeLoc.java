package com.shnok.pathfinding.node;

public class NodeLoc {
    private final int _x;
    private final int _y;
    private final int _z;

    public NodeLoc(int x, int y, int z) {
        _x = x;
        _y = y;
        _z = z;
    }

    public int getX() {
        return _x;
    }

    public int getY() {
        return _y;
    }

    public int getZ() {
        return _z;
    }

    public String toString() {
        return "(" + getX() + "," + getY() + "," + getZ() + ")";
    }

}