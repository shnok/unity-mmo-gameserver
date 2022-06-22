package com.shnok.pathfinding;

import com.shnok.World;
import com.shnok.model.Point3D;
import com.shnok.pathfinding.node.NodeType;
import javolution.util.FastMap;

import java.util.Map;

public class Geodata {
    private static Geodata _instance;
    private Map<Integer, NodeType> _geoData;

    public Geodata() {
        initGeodata();
    }

    public static Geodata getInstance() {
        if (_instance == null) {
            _instance = new Geodata();
        }
        return _instance;
    }

    private void initGeodata() {
        _geoData = new FastMap<>();
        for (int x = -World.WORLD_SIZE; x < World.WORLD_SIZE; x++) {
            for (int z = -World.WORLD_SIZE; z < World.WORLD_SIZE; z++) {
                _geoData.put(flatten(x, 0, z), NodeType.WALKABLE);
            }
        }
        for (int x = -World.WORLD_SIZE + 1; x < World.WORLD_SIZE - 1; x++) {
            _geoData.put(flatten(x, 0, 0), NodeType.UNWALKABLE);
        }

        _geoData.remove(-1);
    }

    public Point3D clampToWorld(Point3D pos) {
        float x = Math.max(-World.WORLD_SIZE + 1, Math.min(World.WORLD_SIZE - 1, pos.getX()));
        float y = Math.max(-World.WORLD_HEIGHT + 1, Math.min(World.WORLD_HEIGHT - 1, pos.getY()));
        float z = Math.max(-World.WORLD_SIZE + 1, Math.min(World.WORLD_SIZE - 1, pos.getZ()));
        return new Point3D(x, y, z);
    }

    public boolean isInsideBounds(int x, int y, int z) {
        return !(x <= -World.WORLD_SIZE || y <= -World.WORLD_HEIGHT || z <= -World.WORLD_SIZE ||
                x >= World.WORLD_SIZE || y >= World.WORLD_HEIGHT || z >= World.WORLD_SIZE);
    }

    public NodeType getNodeType(int x, int y, int z) {
        if (!isInsideBounds(x, y, z)) {
            return NodeType.UNWALKABLE;
        }

        return _geoData.get(flatten(x, y, z));
    }

    public int flatten(int x, int y, int z) {
        return (y * World.WORLD_SIZE * World.WORLD_SIZE) + (z * World.WORLD_SIZE) + x;
    }
}
