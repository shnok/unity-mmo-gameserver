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
        for (int z = -World.WORLD_SIZE; z < World.WORLD_SIZE; z++) {
            for (int x = -World.WORLD_SIZE; x < World.WORLD_SIZE; x++) {
                _geoData.put(flatten(x, 0, z), NodeType.WALKABLE);
            }
        }
        for (int x = -World.WORLD_SIZE + 2; x < World.WORLD_SIZE - 1; x++) {
            _geoData.put(flatten(x, 0, 0), NodeType.UNWALKABLE);
        }
    }

    public Point3D clampToWorld(Point3D pos) {
        float x = Math.max(-World.WORLD_SIZE + 1, Math.min(World.WORLD_SIZE - 1, pos.getX()));
        float y = Math.max(-World.WORLD_HEIGHT + 1, Math.min(World.WORLD_HEIGHT - 1, pos.getY()));
        float z = Math.max(-World.WORLD_SIZE + 1, Math.min(World.WORLD_SIZE - 1, pos.getZ()));
        return new Point3D(x, y, z);
    }

    public boolean isInsideBounds(int x, int y, int z) {
        return (x > -World.WORLD_SIZE && y > -World.WORLD_HEIGHT && z > -World.WORLD_SIZE &&
                x < World.WORLD_SIZE && y < World.WORLD_HEIGHT && z < World.WORLD_SIZE);
    }

    public NodeType getNodeType(int x, int y, int z) {
        int id = flatten(x, y, z);
        if (!isInsideBounds(x, y, z) || !_geoData.containsKey(id)) {
            return NodeType.UNWALKABLE;
        }

        return _geoData.get(id);
    }

    public int flatten(int x, int y, int z) {
        return (y * World.WORLD_HEIGHT * 2 * World.WORLD_SIZE * 2) + (z * World.WORLD_SIZE * 2) + x;
    }
}
