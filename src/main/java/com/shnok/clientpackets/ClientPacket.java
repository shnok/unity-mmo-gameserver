package com.shnok.clientpackets;

import com.shnok.Packet;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class ClientPacket extends Packet {
    private int iterator;

    public ClientPacket(byte[] data) {
        super(data);
        readB();
        readB();
    }

    protected byte readB() {
        return _packetData[iterator++];
    }

    protected int readI() {
        byte[] array = new byte[4];
        System.arraycopy(_packetData, iterator, array, 0, 4);
        iterator += 4;
        return ByteBuffer.wrap(array).getInt();
    }

    protected String readS() {
        byte strLen = readB();
        byte[] data = new byte[strLen];
        System.arraycopy(_packetData, iterator, data, 0, strLen);
        iterator += strLen;

        return new String(data, 0, strLen, StandardCharsets.UTF_8);
    }
}
