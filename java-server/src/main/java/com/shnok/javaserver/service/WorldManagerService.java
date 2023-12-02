package com.shnok.javaserver.service;

import com.shnok.javaserver.model.GameObject;
import com.shnok.javaserver.model.entities.Entity;
import com.shnok.javaserver.model.entities.NpcInstance;
import com.shnok.javaserver.model.entities.PlayerInstance;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Log4j2
public class WorldManagerService {
    public static int WORLD_SIZE = 18;
    public static int WORLD_HEIGHT = 18;
    private static WorldManagerService instance;
    private Map<Integer, PlayerInstance> allPlayers;
    private Map<Integer, NpcInstance> allNPCs;
    private Map<Integer, GameObject> allObjects;
    private AtomicInteger idFactory;

    @Autowired
    public WorldManagerService() {

    }

    public void initialize() {
        log.info("Initializing world manager service.");
        allPlayers = new ConcurrentHashMap<>();
        allNPCs = new ConcurrentHashMap<>();
        allObjects = new ConcurrentHashMap<>();
        idFactory = new AtomicInteger(0);
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
        allPlayers.put(player.getId(), player);
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

    public Map<Integer, NpcInstance> getAllNpcs() {
        return allNPCs;
    }

    public void removePlayer(PlayerInstance player) {
        allPlayers.remove(player.getId());
    }

    public GameObject getPlayer(String name) {
        return allPlayers.get(name);
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
