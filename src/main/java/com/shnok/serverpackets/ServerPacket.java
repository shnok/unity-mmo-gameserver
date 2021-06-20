package com.shnok.serverpackets;

import com.shnok.Packet;

import java.util.Arrays;

public abstract class ServerPacket extends Packet {
    public ServerPacket(byte type) {
        super(type);
    }

    public void buildPacket() {
        if(segments.size() == 0) {
            return;
        }

        int totalSize = 0;
        for(byte[] b : segments) {
            totalSize += b.length;
        }

        _packetLength = (byte)(totalSize + 2);
        byte[] data = new byte[_packetLength];
        data[0] = _packetType;
        data[1] = _packetLength;

        int index = 2;
        for(byte[] s : segments) {
            System.arraycopy(s, 0, data,  index, s.length);
            index += s.length;
        }

        _packetData = data;

        System.out.println("Sent: " + Arrays.toString(_packetData));
    }

    public void buildPacket(byte[] data) {
        _packetLength = (byte)(data.length + 2);
        _packetData = new byte[_packetLength];
        _packetData[0] = _packetType;
        _packetData[1] = _packetLength;
        System.arraycopy(data, 0, _packetData,  2, data.length);
        System.out.println("Sent: " + Arrays.toString(_packetData));
    }
}
