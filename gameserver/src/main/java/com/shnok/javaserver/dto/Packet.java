package com.shnok.javaserver.dto;

public abstract class Packet {
    protected byte packetType;
    protected byte packetLength;
    protected byte[] packetData;

    public Packet(byte type) {
        packetType = type;
    }

    public Packet(byte[] data) {
        setData(data);
    }

    public byte[] getData() {
        return packetData;
    }

    public void setData(byte[] data) {
        packetType = data[0];
        packetLength = data[1];
        packetData = data;
    }

    public byte getType() {
        return packetType;
    }
}
