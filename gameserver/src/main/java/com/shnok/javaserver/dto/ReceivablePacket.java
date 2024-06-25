package com.shnok.javaserver.dto;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class ReceivablePacket extends Packet {
    private int iterator;

    public ReceivablePacket(byte[] data) {
        super(data);
        readB();
        readB();
    }

    protected byte readB() {
        return packetData[iterator++];
    }

    protected int readI() {
        byte[] array = new byte[4];
        System.arraycopy(packetData, iterator, array, 0, 4);
        iterator += 4;
        return ByteBuffer.wrap(array).getInt();
    }

    protected float readF() {
        byte[] array = new byte[4];
        System.arraycopy(packetData, iterator, array, 0, 4);
        iterator += 4;
        return ByteBuffer.wrap(array).getFloat();
    }

    protected String readS() {
        byte strLen = readB();
        byte[] data = new byte[strLen];
        System.arraycopy(packetData, iterator, data, 0, strLen);
        iterator += strLen;

        return new String(data, 0, strLen, StandardCharsets.UTF_8);
    }
}
