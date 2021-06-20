package com.shnok.clientpackets;

import com.shnok.Packet;

public abstract class ClientPacket extends Packet {
    public ClientPacket(byte type) {
        super(type);
    }

    public ClientPacket(byte[] data) {
        super(data);
        parseData();
    }

    public void parseData() {
        for(int i = 0; i < _packetData.length; i++) {
            int nextSegment = (int)_packetData[i];
            if((nextSegment + 1) <= _packetData.length) {
                byte[] segment = new byte[nextSegment];
                System.arraycopy(_packetData, i + 1, segment, 0, nextSegment);
                segments.add(segment);
            } else {
                System.out.println("Error in packet data segments.");
                return;
            }
            i += nextSegment;
        }

        _packetLength = (byte)_packetData.length;
    }
}
