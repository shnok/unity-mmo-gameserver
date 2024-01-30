package com.shnok.javaserver.model;

import com.shnok.javaserver.model.knownlist.ObjectKnownList;
import com.shnok.javaserver.model.position.ObjectPosition;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents all spawnable objects in the world.<BR>
 * <BR>
 * Such as : static object, player, npc, item... <BR>
 * <BR>
 */
@Data
@NoArgsConstructor
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

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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

    /**
     * returns reference to region this object is in
     * @return
     */
    public WorldRegion getWorldRegion()
    {
        return getPosition().getWorldRegion();
    }

}
