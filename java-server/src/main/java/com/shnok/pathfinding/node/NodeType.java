package com.shnok.pathfinding.node;

public enum NodeType {
    NOT_WALKABLE((byte) 0), WALKABLE((byte) 1);

    private final byte value;

    NodeType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
