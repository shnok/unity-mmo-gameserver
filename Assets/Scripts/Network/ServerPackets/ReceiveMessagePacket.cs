using UnityEngine;
using System;
public class ReceiveMessagePacket : ServerPacket {
    private string _text;
    private string _sender;

    public ReceiveMessagePacket(){}
    public ReceiveMessagePacket(byte[] d) : base(d) {
        Parse();
    }
    
    public override void Parse() {    
        try {
            _sender = ReadS(0);
            _text = ReadS(1);
        } catch(Exception e) {
            Debug.Log(e);
        }
    }

    public String GetSender() {
        return _sender;
    }

    public String GetText() {
        return _text;
    }
}