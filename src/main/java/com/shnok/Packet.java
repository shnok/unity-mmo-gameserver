package com.shnok;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class Packet {
    protected byte _packetType;
    protected byte _packetLength;
    protected byte[] _packetData;

    protected List<byte[]> segments = new ArrayList<>();

    public Packet(byte type) {
        _packetType = type;
    }

    public Packet(byte[] data) {
        _packetData = data;
    }

    public byte[] getData() {
        return _packetData;
    }

    public byte getType() {
        return _packetType;
    }

    public void setData(byte[] data) {
        _packetType = (byte)(data.length);
        _packetLength = (byte)(data.length);
        _packetData = data;
    }

    protected void writeS(String s) {
        try {
            if(s != null) {
                byte[] d = s.getBytes(StandardCharsets.UTF_8);
                write(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void writeB(byte b) {
        write(new byte[] {b});
    }

    protected void write(byte[] data) {
        byte[] res = new byte[data.length + 1];
        res[0] = (byte)data.length;
        System.arraycopy(data, 0, res, 1, data.length);
        segments.add(res);
    }

    protected byte readB(int index) {
        return segments.get(index)[0];
    }

    protected String readS(int index) {
        byte[] segment = segments.get(index);

        String s = null;
        s = new String(segment, 0, segment.length, StandardCharsets.UTF_8);

        return s;
    }
}
