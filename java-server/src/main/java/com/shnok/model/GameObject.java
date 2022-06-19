package com.shnok.model;

/**
 * This class represents all spawnable objects in the world.<BR>
 * <BR>
 * Such as : static object, player, npc, item... <BR>
 * <BR>
 */
public abstract class GameObject {
    private int _id;
    private int _model;
    private Point3D _position = new Point3D(0,0,0);

    public GameObject(int id) {
        _id = id;
    }

    public int getId() {
        return _id;
    }

    public float getPosX() {
        return _position.getX();
    }

    public float getPosY() {
        return _position.getY();
    }

    public float getPosZ() {
        return _position.getZ();
    }

    public void setPosition(Point3D pos) {
        _position = pos;
    }
}
