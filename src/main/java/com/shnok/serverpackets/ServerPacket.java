package com.shnok.serverpackets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ServerPacket {
    protected byte _packetType;
    protected byte _packetLength;
    protected byte[] _packetData;

    public ServerPacket(byte type) {
        _packetType = type;
    }

    public byte[] getData() {
        return _packetData;
    }

    public void setData(byte[] data) {
        _packetType = (byte)(data.length);
        _packetLength = (byte)(data.length);
        _packetData = data;
    }

    public void buildPacket(byte[] data) {
        _packetLength = (byte)(data.length + 2);
        _packetData = new byte[_packetLength];
        _packetData[0] = _packetType;
        _packetData[1] = _packetLength;
        for (int i = 2; i < data.length + 2; i++) {
            _packetData[i] = data[i - 2];
        }
    }
}
