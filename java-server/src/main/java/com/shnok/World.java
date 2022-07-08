package com.shnok;

import com.shnok.model.GameObject;
import com.shnok.model.entities.Entity;
import com.shnok.model.entities.NpcInstance;
import com.shnok.model.entities.PlayerInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class World {
    public static int WORLD_SIZE = 18;
    public static int WORLD_HEIGHT = 18;
    private static World _instance;
    private final Map<Integer, PlayerInstance> _allPlayers;
    private final Map<Integer, NpcInstance> _allNPCs;
    private final Map<Integer, GameObject> _allObjects;
    private int _idFactory = 0;

    private World() {
        _allPlayers = new ConcurrentHashMap<>();
        _allNPCs = new ConcurrentHashMap<>();
        _allObjects = new ConcurrentHashMap<>();
    }

    public static World getInstance() {
        if (_instance == null) {
            _instance = new World();
        }
        return _instance;
    }

    public void storeObject(GameObject object) {
        _allObjects.put(object.getId(), object);
    }

    public void removeObject(GameObject object) {
        _allObjects.remove(object);
    }

    public GameObject getObject(int id) {
        return _allObjects.get(id);
    }

    public Entity getEntity(int id) {
        Entity e = _allPlayers.get(id);
        if (e == null) {
            return _allNPCs.get(id);
        }

        return null;
    }

    public void addPlayer(PlayerInstance player) {
        _allPlayers.put(player.getId(), player);
    }

    public void addNPC(NpcInstance npc) {
        _allNPCs.put(npc.getId(), npc);
    }

    public void removeNPC(NpcInstance npc) {
        _allNPCs.remove(npc.getId());
    }

    public Map<Integer, PlayerInstance> getAllPlayers() {
        return _allPlayers;
    }

    public Map<Integer, NpcInstance> getAllNpcs() {
        return _allNPCs;
    }

    public void removePlayer(PlayerInstance player) {
        _allPlayers.remove(player.getId());
    }

    public GameObject getPlayer(String name) {
        return _allPlayers.get(name);
    }

    public int nextID() {
        return _idFactory++;
    }

    public int getIDFactory() {
        return _idFactory;
    }
}
