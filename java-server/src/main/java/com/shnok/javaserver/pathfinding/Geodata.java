package com.shnok.javaserver.pathfinding;

import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.pathfinding.node.NodeType;
import javolution.util.FastMap;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Geodata {
    private static Geodata _instance;
    private Map<Integer, NodeType> _geoData;
    private Object[] values;
    private Object[] keys;

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

            return new DataInputStream(Files.newInputStream(f.toPath()));
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
                _geoData.put(index, type);
            }
        } catch (Exception e) {
            values = _geoData.values().toArray();
            keys = _geoData.keySet().toArray();
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
        for (int z = -WorldManagerService.WORLD_SIZE; z < WorldManagerService.WORLD_SIZE; z++) {
            for (int x = -WorldManagerService.WORLD_SIZE; x < WorldManagerService.WORLD_SIZE; x++) {
                _geoData.put(flatten(x, 0, z), NodeType.WALKABLE);
            }
        }
        for (int x = -WorldManagerService.WORLD_SIZE + 2; x < WorldManagerService.WORLD_SIZE - 1; x++) {
            _geoData.put(flatten(x, 0, 0), NodeType.UNWALKABLE);
        }
    }

    public Point3D clampToWorld(Point3D pos) {
        float x = Math.max(-WorldManagerService.WORLD_SIZE + 1, Math.min(WorldManagerService.WORLD_SIZE - 1, pos.getX()));
        float y = Math.max(-WorldManagerService.WORLD_HEIGHT + 1, Math.min(WorldManagerService.WORLD_HEIGHT - 1, pos.getY()));
        float z = Math.max(-WorldManagerService.WORLD_SIZE + 1, Math.min(WorldManagerService.WORLD_SIZE - 1, pos.getZ()));
        return new Point3D(x, y, z);
    }

    public boolean isInsideBounds(int x, int y, int z) {
        return (x > -WorldManagerService.WORLD_SIZE && y > -WorldManagerService.WORLD_HEIGHT && z > -WorldManagerService.WORLD_SIZE &&
                x < WorldManagerService.WORLD_SIZE && y < WorldManagerService.WORLD_HEIGHT && z < WorldManagerService.WORLD_SIZE);
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
        Random generator = new Random();
        int randomIndex = generator.nextInt(values.length);
        NodeType type = (NodeType) values[randomIndex];

        while (type != NodeType.WALKABLE) {
            randomIndex = generator.nextInt(values.length);
            type = (NodeType) values[randomIndex];
        }

        return parseIndex((int) keys[randomIndex]);
    }

    public Point3D parseIndex(int index) {
        float y = (float) index / (float) Math.pow(WorldManagerService.WORLD_HEIGHT * 2, 2);
        float vy = (y - Math.round(y)) * (float) Math.pow(WorldManagerService.WORLD_HEIGHT * 2, 2);
        float z = vy / (WorldManagerService.WORLD_SIZE * 2);
        float x = (z - Math.round(z)) * (float) (WorldManagerService.WORLD_SIZE * 2);
        System.out.println(Math.round(x) + "," + Math.round(y) + "," + Math.round(z));
        return new Point3D(Math.round(x), Math.round(y), Math.round(z));
    }

    public int flatten(int x, int y, int z) {
        return (y * (int) Math.pow(WorldManagerService.WORLD_HEIGHT * 2, 2)) + (z * WorldManagerService.WORLD_SIZE * 2) + x;
    }
}
