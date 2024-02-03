package com.shnok.javaserver.pathfinding;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.db.entity.ZoneList;
import com.shnok.javaserver.db.repository.ZoneListRepository;
import com.shnok.javaserver.pathfinding.node.Node;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.util.VectorUtils;
import javolution.util.FastMap;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Map;
import java.util.Random;

@Log4j2
public class Geodata {
    private static Geodata instance;
    private Map<String, Map<Point3D, List<Node>>> geoData;
    private Map<String, ZoneList> zoneList;

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
    }

    public void loadGeodata() {
        for(int i = 0; i < Config.ZONES_TO_LOAD.length; i++) {
            String zoneId = Config.ZONES_TO_LOAD[i];
            log.debug("Loading geodata for map {}.", zoneId);
            geoData.put(zoneId, GeodataLoader.getInstance().loadGeodataForMap(zoneId));
        }
    }

    public Point3D getZoneOrigin(String mapId) throws Exception {
        if(!zoneList.containsKey(mapId)) {
            throw new Exception("Zone not found.");
        }

        Point3D zoneOrigin = zoneList.get(mapId).getOrigin();
        return zoneOrigin;
    }

    public String getCurrentZone(Point3D location) throws Exception {
        for(ZoneList z : zoneList.values()) {
            if(z.isInBounds(location)) {
                return z.getId();
            }
        }
        throw new Exception("Outside bounds.");
    }

    public Node getNodeAt(Point3D nodePos) throws Exception {
        return getNodeAt(nodePos, getCurrentZone(nodePos));
    }

    // Get node at a given world position
    public Node getNodeAt(Point3D nodePos, String mapId) throws Exception {
        Point3D nodeIndex = fromWorldToNodePos(nodePos, mapId);

        // Checks if the map index exists
        if(geoData.containsKey(mapId)) {
            Point3D geodataIndex = new Point3D(nodeIndex.getX(), 0, nodeIndex.getZ());
            if(geoData.get(mapId).containsKey(geodataIndex)) {
                List<Node> layers = geoData.get(mapId).get(geodataIndex);
                for (Node layer : layers) {
                    float layerOffset = Math.abs(layer.getNodeIndex().getY() - nodeIndex.getY());
                    //log.debug(layerOffset);
                    if (layerOffset >= 0 && layerOffset <= Config.GEODATA_MAXIMUM_Y_ERROR) {
                        return new Node(layer);
                    }
                }
            }
        }

        throw new Exception("Node not found");
    }

    public Node getClosestNodeAt(Point3D nodePos) throws Exception {
        return getClosestNodeAt(nodePos, getCurrentZone(nodePos));
    }

    // Get node at a given world position
    public Node getClosestNodeAt(Point3D nodePos, String mapId) throws Exception {
        List<Node> layers = getAllNodesAt(nodePos, mapId);

        // find the closest node of given point
        // layers should at the highest be 2-3
        Node closest = null;
        float lowestDiff = -1;
        for(Node n : layers) {
            // verify if the node Y is lower of equals than 4 nodes
            float nodeHeightDiff = Math.abs(n.getCenter().getY() - nodePos.getY());
            if(closest == null || nodeHeightDiff < lowestDiff || lowestDiff == -1) {
                closest = n;
                lowestDiff = nodeHeightDiff;
            }
        }

        return closest;
    }

    public List<Node> getAllNodesAt(Point3D nodePos) throws Exception {
        return getAllNodesAt(nodePos, getCurrentZone(nodePos));
    }

    public List<Node> getAllNodesAt(Point3D nodePos, String mapId) throws Exception {
        Point3D nodeIndex = fromWorldToNodePos(nodePos, mapId);

        // Checks if the map index exists
        if(geoData.containsKey(mapId)) {
            Point3D geodataIndex = new Point3D(nodeIndex.getX(), 0, nodeIndex.getZ());
            if (geoData.get(mapId).containsKey(geodataIndex)) {
                return geoData.get(mapId).get(geodataIndex);
            }
        }

        throw new Exception("Nodes not found");
    }

    public Node findRandomNodeInRange(Point3D center, int nodeRange) throws Exception {
        float range = nodeRange * Config.NODE_SIZE;
        Random r = new Random();

        //closest node y offset
        float lowestDiff = 0;

        // Try to find a random point around the given center 5 times
        for (int i = 0; i < 5; i++) {
            float x = center.getX() + r.nextInt((int) range * 2) - range;
            float z = center.getZ() + r.nextInt((int) range * 2) - range;
            Point3D driftPoint = new Point3D(x, 0, z);

            try {
                return getClosestNodeAt(driftPoint);
            } catch (Exception e) {}
        }

        if(Config.PRINT_PATHFINDER) {
            log.error("Cant find drift: {}", lowestDiff);
        }
        throw new Exception("Couldn't find random drift point");
    }

    public Point3D fromNodeToWorldPos(Point3D nodePos, String mapId) throws Exception {
        Point3D origin = getZoneOrigin(mapId);

        Point3D worldPos = new Point3D(nodePos.getX() * Config.NODE_SIZE + origin.getX(),
                nodePos.getY() * Config.NODE_SIZE + origin.getY(),
                nodePos.getZ() * Config.NODE_SIZE + origin.getZ());

        worldPos = new Point3D(
                VectorUtils.floorToNearest(worldPos.getX(), Config.NODE_SIZE),
                VectorUtils.floorToNearest(worldPos.getY(), Config.NODE_SIZE),
                VectorUtils.floorToNearest(worldPos.getZ(), Config.NODE_SIZE));

        return worldPos;
    }

    public Point3D fromWorldToNodePos(Point3D worldPos, String mapId) throws Exception {
        Point3D origin = getZoneOrigin(mapId);

        Point3D offsetPos = new Point3D(worldPos.getX() - origin.getX(),
                worldPos.getY() - origin.getY(),
                worldPos.getZ() - origin.getZ());

        Point3D nodeId = new Point3D(
                (float) Math.floor(offsetPos.getX() / Config.NODE_SIZE),
                (float) Math.floor(offsetPos.getY() / Config.NODE_SIZE),
                (float) Math.floor(offsetPos.getZ() / Config.NODE_SIZE));

        return nodeId;
    }
}
