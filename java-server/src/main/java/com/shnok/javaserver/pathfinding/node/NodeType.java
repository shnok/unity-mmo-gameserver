package com.shnok.javaserver.pathfinding.node;

public enum NodeType {
    UNWALKABLE((byte) 0), WALKABLE((byte) 1);

    private final byte value;

    NodeType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
