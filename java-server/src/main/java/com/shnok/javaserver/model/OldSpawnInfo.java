package com.shnok.javaserver.model;

import lombok.Data;

@Data
public class OldSpawnInfo {
    private final boolean randomSpawn;
    private int id;
    private int npcId;
    private int respawnDelay;
    private boolean spawned;
    private Point3D spawnPos;

    public OldSpawnInfo(int id, int npcId, int respawnDelay, Point3D spawnPos) {
        this.id = id;
        this.npcId = npcId;
        this.respawnDelay = respawnDelay;
        this.spawnPos = spawnPos;
        spawned = false;
        randomSpawn = false;
    }

    public OldSpawnInfo(int id, int npcId, int respawnDelay) {
        this.id = id;
        this.npcId = npcId;
        this.respawnDelay = respawnDelay;
        spawned = false;
        randomSpawn = true;
    }
}
