package com.shnok.javaserver.model.object;

import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.WorldRegion;
import com.shnok.javaserver.model.knownlist.ObjectKnownList;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.NpcInstance;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.model.position.ObjectPosition;
import com.shnok.javaserver.service.WorldManagerService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Objects;

/**
 * This class represents all spawnable objects in the world.<BR>
 * <BR>
 * Such as : static object, player, npc, item... <BR>
 * <BR>
 */
@Data
@NoArgsConstructor
@Log4j2
public abstract class GameObject {
    protected int id;
    protected int model;
    protected boolean visible = true;
    protected ObjectPosition position;
    protected ObjectKnownList knownList;

    public ObjectKnownList getKnownList() {
        if(knownList == null) {
            knownList = new ObjectKnownList(this);
        }
        return knownList;
    }

    public GameObject(int id) {
        this.id = id;
    }

    public void destroy() {
        log.debug("[{}] Destroying gameObject", getId());

        // Removing self from region
        getPosition().getWorldRegion().removeVisibleObject(this);

        // Tell known list to remove self
        getKnownList().getKnownObjects().forEach(((integer, gameObject) -> {
            gameObject.getKnownList().removeKnownObject(this);
        }));

        // Remove object from all server objects
        WorldManagerService.getInstance().removeObject(this);
    }

    public final ObjectPosition getPosition() {
        if (position == null) {
            position = new ObjectPosition(this);
        }
        return position;
    }

    public void setPosition(Point3D position) {
        getPosition().setXYZ(position.getX(), position.getY(), position.getZ());
    }

    public void setHeading(float heading) {
        getPosition().setHeading(heading);
    }

    public Point3D getPos() {
        return getPosition().getWorldPosition();
    }

    public final float getPosX() {
        return getPosition().getWorldPosition().getX();
    }

    public final float getPosY() {
        return getPosition().getWorldPosition().getY();
    }

    public final float getPosZ() {
        return getPosition().getWorldPosition().getZ();
    }

    public WorldRegion getWorldRegion()
    {
        return getPosition().getWorldRegion();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameObject that = (GameObject) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


    public boolean isEntity() {
        return this instanceof Entity;
    }

    public boolean isItem() {
        return this instanceof ItemInstance;
    }

    public boolean isPlayer() {
        return this instanceof PlayerInstance;
    }

    public boolean isNpc() {
        return this instanceof NpcInstance;
    }
}
