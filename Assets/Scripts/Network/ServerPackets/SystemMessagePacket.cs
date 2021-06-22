using UnityEngine;
using System;

public enum MessageType {
    USER_LOGGED_IN,
    USER_LOGGED_OFF
}

public class SystemMessagePacket : ServerPacket {

    private MessageType type;
    private SystemMessage message;

    public SystemMessagePacket(){}
    public SystemMessagePacket(byte[] d) : base(d) {
        Parse();
    }
    
    public override void Parse() {    
        try {
            type = (MessageType)ReadB();

            switch (type) {
                case MessageType.USER_LOGGED_IN:
                    message = new MessageLoggedIn(ReadS());
                    break;
                case MessageType.USER_LOGGED_OFF:
                    message = new MessageLoggedOut(ReadS());
                    break;
            }            
        } catch(Exception e) {
            Debug.Log(e);
        }
    }

    public SystemMessage GetMessage() {
        return message;
    }
}