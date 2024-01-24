package com.shnok.javaserver.pathfinding;

import com.shnok.javaserver.db.entity.ZoneList;
import com.shnok.javaserver.db.interfaces.ZoneListDao;
import com.shnok.javaserver.db.repository.ZoneListRepository;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.pathfinding.node.NodeType;
import com.shnok.javaserver.util.VectorUtils;
import javolution.util.FastMap;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class Geodata {
    private static Geodata instance;
    private Map<String, Map<Point3D, NodeType>> geoData;
    private Map<String, ZoneList> zoneList;
    private Object[] values;
    private Object[] keys;
    private float nodeSize = 0.5f;

    public Geodata() {
        initGeodata();
    }

    public static Geodata getInstance() {
        if (instance == null) {
            instance = new Geodata();
        }
        return instance;
    }

    private void initGeodata() {
        ZoneListRepository zoneListRepository = new ZoneListRepository();
        zoneList = zoneListRepository.getAllZoneMap();
        geoData = new FastMap<>();
     //   loadFromFile();
    }

    public Point3D getZoneOrigin(String mapId) throws Exception {
        if(!zoneList.containsKey(mapId)) {
            throw new Exception("Zone not found.");
        }

        Point3D zoneOrigin = zoneList.get(mapId).getOrigin();
        return zoneOrigin;
    }

   /* private void loadFromFile() {
        DataInputStream is = openFile("geodata/terrain.dat");
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
                geoData.put(index, type);
            }
        } catch (Exception e) {
            values = geoData.values().toArray();
            keys = geoData.keySet().toArray();
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
                geoData.put(flatten(x, 0, z), NodeType.WALKABLE);
            }
        }
        for (int x = -WorldManagerService.WORLD_SIZE + 2; x < WorldManagerService.WORLD_SIZE - 1; x++) {
            geoData.put(flatten(x, 0, 0), NodeType.UNWALKABLE);
        }
    }*/

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
        /*int id = flatten(x, y, z);
        if (!isInsideBounds(x, y, z) || !geoData.containsKey(id)) {
            return NodeType.UNWALKABLE;
        }

        return geoData.get(id);*/
        return null;
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

    public Point3D fromNodeToWorldPos(Point3D nodePos, String mapId) throws Exception {
        Point3D origin = getZoneOrigin(mapId);

        Point3D worldPos = new Point3D(nodePos.getX() * nodeSize + origin.getX(),
                nodePos.getY() * nodeSize + origin.getY(),
                nodePos.getZ() * nodeSize + origin.getZ());

        worldPos = new Point3D(
                VectorUtils.floorToNearest(worldPos.getX(), nodeSize),
                VectorUtils.floorToNearest(worldPos.getY(), nodeSize),
                VectorUtils.floorToNearest(worldPos.getZ(), nodeSize));
        return worldPos;
    }

    public Point3D fromWorldToNodePos(Point3D worldPos, String mapId) throws Exception {
        Point3D origin = getZoneOrigin(mapId);

        Point3D offsetPos = new Point3D(worldPos.getX() - origin.getX(),
                worldPos.getY() - origin.getY(),
                worldPos.getZ() - origin.getZ());

        Point3D nodePos = new Point3D(
                (float) Math.floor(offsetPos.getX() / nodeSize),
                (float) Math.floor(offsetPos.getY() / nodeSize),
                (float) Math.floor(offsetPos.getZ() / nodeSize));

        return nodePos;
    }

    /*public Node GetNode(int x, int y, int z) {
        Node node;
        nodes.TryGetValue(new Vector3(x, y, z), out node);
        return node;
    }*/
}
