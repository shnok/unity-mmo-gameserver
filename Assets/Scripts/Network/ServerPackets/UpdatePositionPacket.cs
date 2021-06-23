using UnityEngine;
using System;
public class UpdatePositionPacket : ServerPacket {
    private int _id;
    private Vector3 _pos = new Vector3();

    public UpdatePositionPacket(){}
    public UpdatePositionPacket(byte[] d) : base(d) {
        Parse();
    }
    
    public override void Parse() {    
        try {
            _id = ReadI();
            _pos.x = (float) ReadI();
            _pos.y = (float) ReadI();
            _pos.z = (float) ReadI();
        } catch(Exception e) {
            Debug.Log(e);
        }
    }

    public int getId() {
        return _id;
    }

    public Vector3 getPosition() {
        return _pos;
    }
}