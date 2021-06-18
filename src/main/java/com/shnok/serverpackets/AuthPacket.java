package com.shnok.serverpackets;

public class AuthPacket extends ServerPacket {

    public enum AuthReason {
        ALLOW,
        ALREADY_CONNECTED,
        INVALID_USERNAME
    }

    public AuthPacket(AuthReason reason) {
        super((byte)0x01);
        buildPacket(new byte[] { (byte)reason.ordinal() });
    }
}
