package com.shnok;

public abstract class Packet {
    protected byte _packetType;
    protected byte _packetLength;
    protected byte[] _packetData;

    public Packet(byte type) {
        _packetType = type;
    }

    public Packet(byte[] data) {
        setData(data);
    }

    public byte[] getData() {
        return _packetData;
    }

    public byte getType() {
        return _packetType;
    }

    public void setData(byte[] data) {
        _packetType = data[0];
        _packetLength = data[1];
        _packetData = data;
    }
}
