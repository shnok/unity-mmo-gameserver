package com.shnok.clientpackets;

public class AuthRequest extends ClientPacket {

    private final String _username;

    public AuthRequest(byte[] data) {
        super(data);

        _username = readS();
    }

    public String getUsername() {
        return _username;
    }
}
