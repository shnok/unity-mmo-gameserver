package com.shnok.pathfinding;

import com.shnok.World;
import com.shnok.pathfinding.node.NodeType;
import javolution.util.FastMap;

import java.util.Map;

public class Geodata
{
    private Map<Integer, NodeType> _geoData;
    private static Geodata _instance;
    public static Geodata getInstance()
    {
        if (_instance == null)
        {
            _instance = new Geodata();
        }
        return _instance;
    }

    public Geodata()
    {
        initGeodata();
    }

    private void initGeodata() {
        _geoData = new FastMap<>();
        for(int x = 0; x < World.WORLD_SIZE; x++) {
            for(int z = 0; z < World.WORLD_SIZE; z++) {
                _geoData.put(flatten(x, 0, z), NodeType.WALKABLE);
            }
        }
        for(int x = 1; x < World.WORLD_SIZE-1; x++) {
            _geoData.put(flatten(x, 0, 3), NodeType.NOT_WALKABLE);
        }

        _geoData.remove(-1);
    }

    public int toWorldCoord(int coord) {
        if(coord < 0)
            return (World.WORLD_SIZE/2) + coord;
        return coord - (World.WORLD_SIZE/2);
    }
    public NodeType getNodeType(int x, int y, int z) {
        NodeType n = _geoData.get(flatten(x, y, z));
        return n;
    }

    public int flatten(int x, int y, int z) {
        if(x < 0 || y < 0 || z < 0 || x >= World.WORLD_SIZE || y >= World.WORLD_HEIGHT || z >= World.WORLD_SIZE)
            return -1;
        return (y * World.WORLD_SIZE * World.WORLD_SIZE) + (z * World.WORLD_SIZE) + x;
    }
}
