package com.shnok.model.entities;

import com.shnok.model.status.Status;
import com.shnok.model.GameObject;

/**
 * This class represents all entities in the world.<BR>
 * <BR>
 * Such as : Players or AIs<BR>
 * <BR>
 */
public  abstract class Entity extends GameObject {
    public Entity(int id) {
        super(id);
    }

    public abstract void inflictDamage(int value);

    public abstract Status getStatus();

    public abstract void setStatus(Status status);
}
