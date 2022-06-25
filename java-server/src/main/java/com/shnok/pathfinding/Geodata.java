package com.shnok.pathfinding;

import com.shnok.World;
import com.shnok.model.Point3D;
import com.shnok.pathfinding.node.NodeType;
import javolution.util.FastMap;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

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
        loadFromFile();
    }

    private void loadFromFile() {
        DataInputStream is = openFile("terrain.dat");
        if (is != null) {
            parseFile(is);
        }
    }

    private DataInputStream openFile(String fileName) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File f = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());
            DataInputStream in = new DataInputStream(Files.newInputStream(f.toPath()));

            return in;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void parseFile(DataInputStream in) {
        try {
            int index;
            while (true) {
                index = swapint(in.readInt());
                NodeType type = NodeType.values()[in.readByte()];
                System.out.println(index + "," + type);
                _geoData.put(index, type);
            }
        } catch (Exception e) {
        }
    }

    private int swapint(int intvalue) {
        int byte0 = ((intvalue >> 24) & 0xFF);
        int byte1 = ((intvalue >> 16) & 0xFF);
        int byte2 = ((intvalue >> 8) & 0xFF);
        int byte3 = (intvalue & 0xFF);
        return (byte3 << 24) + (byte2 << 16) + (byte1 << 8) + (byte0);
    }

    private void generateData() {
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

    /* Find a valid location based on geodata */
    public Point3D randomLocation() {
        int iterations = 0;

        /* Loop until valid location found */
        while (iterations < 20) {
            int layer = 0;
            int[] layers = new int[World.WORLD_HEIGHT * 2];
            int randomX = (int) (Math.random() * (World.WORLD_SIZE + 1) - (float) World.WORLD_SIZE / 2f);
            int randomZ = (int) (Math.random() * (World.WORLD_SIZE + 1) - (float) World.WORLD_SIZE / 2f);

            /* find layers */
            for (int y = -World.WORLD_HEIGHT; y < World.WORLD_HEIGHT; y++) {
                if (getNodeType(randomX, y, randomZ) == NodeType.WALKABLE) {
                    layers[layer] = y;
                    layer++;
                }
            }

            /* found a layer */
            if (layer != 0) {
                layer = (int) (Math.random() * layer);
                return new Point3D(randomX, layers[layer], randomZ);
            }

            iterations++;
        }

        return new Point3D(0, 0, 0);
    }

    public int flatten(int x, int y, int z) {
        return (y * World.WORLD_HEIGHT * 2 * World.WORLD_SIZE * 2) + (z * World.WORLD_SIZE * 2) + x;
    }
}
