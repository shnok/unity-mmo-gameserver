package com.shnok.model.entities;

import com.shnok.model.status.Status;
import com.shnok.model.GameObject;
import com.shnok.pathfinding.PathFinding;

/**
 * This class represents all entities in the world.<BR>
 * <BR>
 * Such as : Players or AIs<BR>
 * <BR>
 */
public  abstract class Entity extends GameObject {
    boolean _canMove = true;
    public Entity(int id) {
        super(id);
    }

    public abstract void inflictDamage(int value);

    public abstract Status getStatus();

    public abstract void setStatus(Status status);

    public abstract boolean canMove();

    public abstract void moveTo(int x, int y, int z);

    public abstract void onDeath();
}
