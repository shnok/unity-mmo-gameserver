using UnityEngine;
using System;

public class PlayerInfoPacket : ServerPacket {

    private NetworkIdentity _identity = new NetworkIdentity();
    private PlayerStatus _status = new PlayerStatus();

    public PlayerInfoPacket(){}
    public PlayerInfoPacket(byte[] d) : base(d) {
        Parse();
    }
    
    public override void Parse() {    
        try {
            _identity.SetId(ReadI());
            _identity.SetName(ReadS());
            _identity.SetPosX(ReadI());
            _identity.SetPosY(ReadI());
            _identity.SetPosZ(ReadI());
            _status.SetLevel(ReadI());
            _status.SetHp(ReadI());
            _status.SetMaxHp(ReadI());
            _status.SetStamina(ReadI());
            _status.SetMaxStamina(ReadI());
        } catch(Exception e) {
            Debug.Log(e);
        }
    }

    public NetworkIdentity GetIdentity() {
        return _identity;
    }

    public PlayerStatus GetStatus() {
        return _status;
    }
}