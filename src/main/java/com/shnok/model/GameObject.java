package com.shnok.model;

/**
 * This class represents all spawnable objects in the world.<BR>
 * <BR>
 * Such as : static object, player, npc, item... <BR>
 * <BR>
 */
public abstract class GameObject {
    private String _name;
    private int _id;
    private int _model;
    private Point3D _position = new Point3D(0,0,0);

    public GameObject(int id) {
        _id = id;
    }

    public GameObject(int id, String name) {
        _id = id;
        _name = name;
    }

    public int getId() {
        return _id;
    }

    public int getPosX() {
        return _position.getX();
    }

    public int getPosY() {
        return _position.getY();
    }

    public int getPosZ() {
        return _position.getZ();
    }

    public String getName() {
        return _name;
    }

    public void setPosition(Point3D pos) {
        _position = pos;
    }
}
