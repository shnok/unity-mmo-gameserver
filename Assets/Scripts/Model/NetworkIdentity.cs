using System.Collections;
using System.Collections.Generic;
using UnityEngine;

[System.Serializable]
public class NetworkIdentity
{
    public int _id;
    public string _name;
    public int _model;
    public Vector3 _position = new Vector3(0,0,0);
    public bool owned = false;

    public NetworkIdentity() {}
    public NetworkIdentity(int id) {
        _id = id;
    }

    public NetworkIdentity(int id, string name) {
        _id = id;
        _name = name;
    }

    public int GetId() {
        return _id;
    }

    public void SetId(int id) {
        _id = id;
    }

    public string GetName() {
        return _name;
    }

    public void SetName(string name) {
        _name = name;
    }

    public void SetPosX(float x) {
        _position.x = x;
    }

    public void SetPosY(float y) {
        _position.y = y;
    }
    public void SetPosZ(float z) {
        _position.z = z;
    }

    public int GetPosX() {
        return (int) _position.x;
    }

    public int GetPosY() {
        return (int) _position.y;
    }

    public int GetPosZ() {
        return (int) _position.z;
    }

    public Vector3 GetPosition() {
        return _position;
    }

    public void SetOwned(bool value) {
        owned = value;
    }

    public bool IsOwned() {
        return owned;
    }
}
