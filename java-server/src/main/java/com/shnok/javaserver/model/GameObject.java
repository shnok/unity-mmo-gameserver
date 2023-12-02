package com.shnok.javaserver.model;

/**
 * This class represents all spawnable objects in the world.<BR>
 * <BR>
 * Such as : static object, player, npc, item... <BR>
 * <BR>
 */
public abstract class GameObject {
    protected int id;
    protected int model;
    protected Point3D position = new Point3D(0, 0, 0);

    public GameObject() {
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

    public float getPosX() {
        return position.getX();
    }

    public float getPosY() {
        return position.getY();
    }

    public float getPosZ() {
        return position.getZ();
    }

    public Point3D getPos() {
        return position;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

}
