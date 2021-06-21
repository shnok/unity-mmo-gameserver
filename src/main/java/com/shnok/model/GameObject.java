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
    private Point3D _position;

    public GameObject(int id) {
        _id = id;
        _position = new Point3D(0,0,0);
    }

    public GameObject(int id, String name) {
        _id = id;
        _name = name;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }
}
