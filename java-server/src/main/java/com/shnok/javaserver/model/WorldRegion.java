package com.shnok.javaserver.model;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.model.entity.Entity;
import com.shnok.javaserver.model.entity.PlayerInstance;
import javolution.util.FastList;
import lombok.extern.log4j.Log4j2;

import java.util.Iterator;

@Log4j2
public class WorldRegion {
    private final FastList<GameObject> visibleObjects;
    private final FastList<WorldRegion> surroundingRegions;
    private final FastList<PlayerInstance> allPlayers;
    private final int tileX, tileY;

    public WorldRegion(int tileX, int tileY) {
        allPlayers = new FastList<>();
        surroundingRegions = new FastList<>();
        visibleObjects = new FastList<>();
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public void addVisibleObject(GameObject object) {
        if (object == null) {
            return;
        }

        if(Config.PRINT_WORLDREGION) {
            log.debug("Adding visible object {} to region {}.", object.getId(), getName());
        }
        visibleObjects.add(object);

        if (object instanceof PlayerInstance)
        {
            allPlayers.add((PlayerInstance) object);
        }
    }

    public void removeVisibleObject(GameObject object) {
        if (object == null) {
            return;
        }

        if(Config.PRINT_WORLDREGION) {
            log.debug("Removing visible object {} to region {}.", object.getId(), getName());
        }

        visibleObjects.remove(object);

        // Should force visible objects to recheck surroundings
        visibleObjects.forEach((visible) -> {
            //visible.getKnownList().removeKnownObject(object);
            visible.getKnownList().forceRecheckSurroundings();
        });

        if (object instanceof PlayerInstance) {
            allPlayers.remove((PlayerInstance) object);
        }
    }

    public void addSurroundingRegion(WorldRegion region) {
        surroundingRegions.add(region);
    }

    public FastList<WorldRegion> getSurroundingRegions() {
        return surroundingRegions;
    }

    public Iterator<PlayerInstance> iterateAllPlayers()
    {
        return allPlayers.iterator();
    }

    public FastList<GameObject> getVisibleObjects() {
        return visibleObjects;
    }

    public String getName() {
        return "(" + tileX + ", " + tileY + ")";
    }
}
