package com.shnok.javaserver.dto;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class ServerPacket extends Packet {
    private final List<Byte> buffer = new ArrayList<>();

    public ServerPacket(byte type) {
        super(type);
    }

    protected void writeS(String s) {
        try {
            if (s != null) {
                byte[] d = s.getBytes(StandardCharsets.UTF_8);
                write(d);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void writeB(byte b) {
        buffer.add(b);
    }

    protected void writeI(int i) {
        Byte[] array = new Byte[]{(byte) ((i >> 24) & 0xff),
                (byte) ((i >> 16) & 0xff),
                (byte) ((i >> 8) & 0xff),
                (byte) ((i) & 0xff)};

        buffer.addAll(Arrays.asList(array));
    }

    protected void writeF(float f) {
        int intBits = Float.floatToIntBits(f);
        Byte[] array = new Byte[]{
                (byte) (intBits >> 24), (byte) (intBits >> 16), (byte) (intBits >> 8), (byte) (intBits)};

        buffer.addAll(Arrays.asList(array));
    }

    protected void write(byte[] data) {

        buffer.add((byte) data.length);
        List<Byte> byteList = IntStream.range(0, data.length)
                .mapToObj((int j) -> data[j])
                .collect(Collectors.toList());
        buffer.addAll(byteList);
    }

    protected void buildPacket() {
        buffer.add(0, packetType);
        buffer.add(1, (byte) (buffer.size() + 1));
        Byte[] array = buffer.toArray(new Byte[0]);
        setData(ArrayUtils.toPrimitive(array));

        //System.out.println("Sent: " + Arrays.toString(_packetData));
    }
}
