package com.shnok.javaserver.service;

import com.shnok.javaserver.model.object.GameObject;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.WorldRegion;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.NpcInstance;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import javolution.util.FastList;
import lombok.extern.log4j.Log4j2;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class WorldManagerService {
    public WorldRegion[][] worldRegions;

    /** Map dimensions */
    public static final int MAP_MIN_Y = -2496;
    public static final int MAP_MAX_Y = 4354;
    public static final int MAP_MIN_X = -4993;
    public static final int MAP_MAX_X = 4993;
    public static final int SHIFT_BY = 6;
    public static final int OFFSET_X = Math.abs(MAP_MIN_X >> SHIFT_BY);
    public static final int OFFSET_Y = Math.abs(MAP_MIN_Y >> SHIFT_BY);
    private static final int REGIONS_X = (MAP_MAX_X >> SHIFT_BY) + OFFSET_X;
    private static final int REGIONS_Y = (MAP_MAX_Y >> SHIFT_BY) + OFFSET_Y;

    /* to remove */
    public static final int WORLD_SIZE = 18;
    public static final int WORLD_HEIGHT = 18;

    private static WorldManagerService instance;

    private Map<Integer, PlayerInstance> allPlayers;
    private Map<Integer, NpcInstance> allNPCs;
    private Map<Integer, GameObject> allObjects;
    private AtomicInteger idFactory;

    public static WorldManagerService getInstance() {
        if (instance == null) {
            instance = new WorldManagerService();
        }
        return instance;
    }

    public void initialize() {
        log.info("Initializing world manager service.");
        allPlayers = new ConcurrentHashMap<>();
        allNPCs = new ConcurrentHashMap<>();
        allObjects = new ConcurrentHashMap<>();
        idFactory = new AtomicInteger(0);
        initRegions();
    }

    private void initRegions()
    {
        log.info("Setting up World Regions");

        worldRegions = new WorldRegion[REGIONS_X + 1][REGIONS_Y + 1];
        for (int i = 0; i <= REGIONS_X; i++) {
            for (int j = 0; j <= REGIONS_Y; j++) {
                worldRegions[i][j] = new WorldRegion(i, j);
            }
        }

        for (int x = 0; x <= REGIONS_X; x++) {
            for (int y = 0; y <= REGIONS_Y; y++) {
                for (int a = -1; a <= 1; a++) {
                    for (int b = -1; b <= 1; b++) {
                        if (validRegion(x + a, y + b)) {
                            worldRegions[x + a][y + b].addSurroundingRegion(worldRegions[x][y]);
                        }
                    }
                }
            }
        }

        log.info("World: (" + REGIONS_X + " by " + REGIONS_Y + ") World Region Grid set up.");
    }

    private boolean validRegion(int x, int y) {
        return ((x >= 0) && (x <= REGIONS_X) && (y >= 0) && (y <= REGIONS_Y));
    }

    public WorldRegion getRegion(Point3D point) {
        int x = ((int) point.getX() >> SHIFT_BY) + OFFSET_X;
        int y = ((int) point.getZ() >> SHIFT_BY) + OFFSET_Y;
        //log.debug("Region at {} is [{}][{}]", point, x, y);
        return worldRegions[x][y];
    }

    public WorldRegion getRegion(int x, int y) {
        return worldRegions[x][y];
    }

    public void storeObject(GameObject object) {
        allObjects.put(object.getId(), object);
    }

    public void removeObject(GameObject object) {
        allObjects.remove(object);
    }

    public GameObject getObject(int id) {
        return allObjects.get(id);
    }

    public Entity getEntity(int id) {
        Entity e = allPlayers.get(id);
        if (e == null) {
            return allNPCs.get(id);
        }

        return null;
    }

    public void addPlayer(PlayerInstance player) {
        allPlayers.put(player.getCharId(), player);
    }

    public void addNPC(NpcInstance npc) {
        allNPCs.put(npc.getId(), npc);
    }

    public void removeNPC(NpcInstance npc) {
        allNPCs.remove(npc.getId());
    }

    public Map<Integer, PlayerInstance> getAllPlayers() {
        return allPlayers;
    }

    public Map<Integer, PlayerInstance> getNearbyPlayers() {
        return null;
    }

    public Map<Integer, NpcInstance> getAllNpcs() {
        return allNPCs;
    }

    public void removePlayer(PlayerInstance player) {
        allPlayers.remove(player.getCharId());
    }

    public PlayerInstance getPlayer(int id) {
        return allPlayers.get(id);
    }

    public FastList<GameObject> getVisibleObjects(GameObject target) {
        WorldRegion reg = target.getWorldRegion();
        if (reg == null) {
            return null;
        }

        // Create an FastList in order to contain all visible L2Object
        FastList<GameObject> result = new FastList<>();

        // Create a FastList containing all regions around the current region
        FastList<WorldRegion> regions = reg.getSurroundingRegions();

        // Go through the FastList of region
        for (int i = 0; i < regions.size(); i++) {
            // Go through visible objects of the selected region
            for (GameObject gameObject : regions.get(i).getVisibleObjects()) {
                if (gameObject == null) {
                    continue;
                }
                if (gameObject.getId() == target.getId()) {
                    continue; // skip our own character
                }
                if (!gameObject.isVisible()) {
                    continue;
                }

                result.add(gameObject);
            }
        }
        return result;
    }

    public FastList<PlayerInstance> getVisiblePlayers(GameObject target) {
        WorldRegion reg = target.getWorldRegion();

        if (reg == null) {
            return null;
        }

        // Create an FastList in order to contain all visible L2Object
        FastList<PlayerInstance> result = new FastList<>();

        // Create a FastList containing all regions around the current region
        FastList<WorldRegion> regions = reg.getSurroundingRegions();

        // Go through the FastList of region
        for (int i = 0; i < regions.size(); i++) {
            // Create an Iterator to go through the visible L2Object of the L2WorldRegion
            Iterator<PlayerInstance> players = regions.get(i).iterateAllPlayers();

            // Go through visible object of the selected region
            while (players.hasNext()) {
                PlayerInstance object = players.next();

                if (object == null) {
                    continue;
                }

                if (object.equals(target)) {
                    continue; // skip our own character
                }

                if (!object.isVisible()){
                    continue;
                }

                result.add(object);
            }
        }

        return result;
    }

    public int nextID() {
        return idFactory.getAndIncrement();
    }

    public void incrementID() {
        idFactory.incrementAndGet();
    }

    public int getIDFactory() {
        return idFactory.get();
    }
}
