package com.shnok.javaserver.model.position;

import com.shnok.javaserver.model.object.GameObject;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.WorldRegion;
import com.shnok.javaserver.service.WorldManagerService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
public class ObjectPosition
{
    private final GameObject activeObject;
    private float heading = 0;
    private Point3D worldPosition;
    private Point3D lastWorldPosition;
    private WorldRegion worldRegion; // Object localization : Used for items/chars that are seen in the world

    public ObjectPosition(GameObject activeObject) {
        this.activeObject = activeObject;
        setWorldRegion(WorldManagerService.getInstance().getRegion(getWorldPosition()));
    }

    public final void setXYZ(float x, float y, float z) {
        setWorldPosition(x, y, z);

        if (WorldManagerService.getInstance().getRegion(getWorldPosition()) != getWorldRegion()) {
            updateWorldRegion();
        }
    }

    /**
     * checks if current object changed its region, if so, update referencies
     */
    public void updateWorldRegion() {
        if (!getActiveObject().isVisible()) {
            return;
        }

        WorldRegion newRegion = WorldManagerService.getInstance().getRegion(getWorldPosition());
        if (newRegion != getWorldRegion()) {
            getWorldRegion().removeVisibleObject(getActiveObject());

            setWorldRegion(newRegion);

            // Add the L2Oject spawn to _visibleObjects and if necessary to _allplayers of its L2WorldRegion
            getWorldRegion().addVisibleObject(getActiveObject());
        }
    }

    public Point3D getWorldPosition() {
        if (worldPosition == null) {
            worldPosition = new Point3D(0, 0, 0);
        }
        return worldPosition;
    }

    public Point3D getLastWorldPosition() {
        if (lastWorldPosition == null) {
            lastWorldPosition = new Point3D(0, 0, 0);
        }
        return lastWorldPosition;
    }

    public void setLastWorldPosition(Point3D pos) {
        lastWorldPosition.setX(pos.getX());
        lastWorldPosition.setY(pos.getY());
        lastWorldPosition.setZ(pos.getZ());
    }

    public void setWorldPosition(float x, float y, float z) {
        getWorldPosition().setXYZ(x, y, z);
    }

    public final void setWorldPosition(Point3D newPosition) {
        setWorldPosition(newPosition.getX(), newPosition.getY(), newPosition.getZ());
    }

    public final WorldRegion getWorldRegion()
    {
        return worldRegion;
    }

    public final void setWorldRegion(WorldRegion value) {
        worldRegion = value;
    }
}