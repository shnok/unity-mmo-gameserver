package com.shnok;

import com.shnok.model.GameObject;
import com.shnok.model.PlayerInstance;

import java.util.HashMap;
import java.util.Map;

public class World {
    private static World _instance = new World();
    private final Map<String, PlayerInstance> _allPlayers;
    private final Map<Integer, GameObject> _allObjects;
    private int _idFactory = 0;

    private World() {
        _allPlayers = new HashMap<String, PlayerInstance>();
        _allObjects = new HashMap<Integer, GameObject>();
    }

    public static World getInstance() {
        if(_instance == null) {
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

    public GameObject findObject(int id) {
        return _allObjects.get(id);
    }

    public void addPlayer(PlayerInstance player) {
        _allPlayers.put(player.getName(), player);
    }

    public void removePlayer(GameObject player) {
        _allPlayers.remove(player);
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
