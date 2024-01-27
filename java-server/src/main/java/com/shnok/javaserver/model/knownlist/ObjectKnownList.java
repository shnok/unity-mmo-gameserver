package com.shnok.javaserver.model.knownlist;

import com.shnok.javaserver.model.GameObject;
import com.shnok.javaserver.model.entity.Entity;
import com.shnok.javaserver.model.entity.PlayerInstance;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.util.VectorUtils;
import javolution.util.FastMap;
import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.Map;

@Log4j2
public class ObjectKnownList {
    private final GameObject activeObject;
    private Map<Integer, GameObject> knownObjects;

    public ObjectKnownList(GameObject activeObject) {
        this.activeObject = activeObject;
    }

    public GameObject getActiveObject() {
        return activeObject;
    }

    public boolean knowsObject(GameObject object) {
        return (getActiveObject() == object) || getKnownObjects().containsKey(object.getId());
    }

    public boolean addKnownObject(GameObject object) {
        return addKnownObject(object, null);
    }

    public boolean addKnownObject(GameObject object, Entity dropper) {
        if ((object == null) || (object == getActiveObject())) {
            return false;
        }

        // Check if already know object
        if (knowsObject(object)) {
            if (!object.isVisible()) {
                removeKnownObject(object);
            }
            return false;
        }

        // Check if object is not inside distance to watch object
        if (!VectorUtils.checkIfInRange(getDistanceToWatchObject(object), getActiveObject(), object)) {
            return false;
        }

        return (getKnownObjects().put(object.getId(), object) == null);
    }

    public Map<Integer, GameObject> getKnownObjects() {
        if (knownObjects == null) {
            knownObjects = new FastMap<Integer, GameObject>().shared();
        }
        return knownObjects;
    }

    public void removeAllKnownObjects()
    {
        getKnownObjects().clear();
    }

    public boolean removeKnownObject(GameObject object) {
        if (object == null || !knownObjects.containsKey(object.getId())) {
            return false;
        }

        log.debug("[{}] Remove known object {}", activeObject.getId(), object.getId());

        return (getKnownObjects().remove(object.getId()) != null);
    }

    public final synchronized void updateKnownObjects() {
        if (getActiveObject() instanceof Entity) {
            findCloseObjects();
            forgetObjects();
            //log.debug("[{}] Known objects count: {}", getActiveObject().getId(),knownObjects.size());
        }
    }

    private void findCloseObjects() {
        boolean isPlayer = (getActiveObject() instanceof PlayerInstance);

        if (isPlayer) {
            Collection<GameObject> objects = WorldManagerService.getInstance().getVisibleObjects(getActiveObject());
            if (objects == null) {
                return;
            }

            // Go through all visible GameObject near the Player
            for (GameObject object : objects) {
                if (object == null) {
                    continue;
                }

                addKnownObject(object);

                if (object instanceof Entity) {
                    object.getKnownList().addKnownObject(getActiveObject());
//                    log.debug("[{}] Request add entity to {} knownlist", getActiveObject().getId(), object.getId());
                }
                if (object instanceof PlayerInstance) {
                    object.getKnownList().addKnownObject(getActiveObject());
//                    log.debug("[{}] Request add player to {} knownlist", getActiveObject().getId(), object.getId());
                }
            }
        } else {
            Collection<PlayerInstance> players = WorldManagerService.getInstance().getVisiblePlayers(getActiveObject());
            if (players == null) {
                return;
            }

            // Go through all visible players near the entity
            for (GameObject object : players) {
                if (object == null) {
                    continue;
                }

                addKnownObject(object);
                log.debug("[{}] Request add entity to {} knownlist", getActiveObject().getId(), object.getId());
            }
        }
    }

    private void forgetObjects() {
        Collection<GameObject> knownObjects = getKnownObjects().values();

        if (knownObjects.size() == 0) {
            return;
        }

        for (GameObject object : knownObjects) {
            if (object == null) {
                continue;
            }

            int distanceToForgetObject = getDistanceToForgetObject(object);
            if (!object.isVisible() || !VectorUtils.checkIfInRange(distanceToForgetObject, getActiveObject(), object)) {
                removeKnownObject(object);
                log.debug("[{}] Remove known object: {}", getActiveObject().getId(), object.getId());
            }
        }
    }

    public int getDistanceToForgetObject(GameObject object) {
        return 0;
    }

    public int getDistanceToWatchObject(GameObject object) {
        return 0;
    }

    public void forceRecheckSurroundings() {
        ThreadPoolManagerService.getInstance().execute(
                new ObjectKnownList.KnownListAsynchronousUpdateTask(activeObject));
    }

    public static class KnownListAsynchronousUpdateTask implements Runnable {
        private final GameObject obj;

        public KnownListAsynchronousUpdateTask(GameObject obj){
            this.obj = obj;
        }

        @Override
        public void run() {
            if (obj != null) {
//                log.debug("[{}] Updating known objects...", obj.getId());
                obj.getKnownList().updateKnownObjects();
            }
        }
    }
}
