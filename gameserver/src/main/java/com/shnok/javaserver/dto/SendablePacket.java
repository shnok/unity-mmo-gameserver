package com.shnok.javaserver.dto;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
public abstract class SendablePacket extends Packet {
    private final List<Byte> buffer = new ArrayList<>();

    public SendablePacket(byte type) {
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

    protected void writeB(byte[] b) {
        for (byte bb : b) {
            buffer.add(bb);
        }
    }

    protected void writeI(int i) {
        Byte[] array = new Byte[]{(byte) ((i >> 24) & 0xff),
                (byte) ((i >> 16) & 0xff),
                (byte) ((i >> 8) & 0xff),
                (byte) ((i) & 0xff)};

        buffer.addAll(Arrays.asList(array));
    }

    protected void writeL(long l) {
        Byte[] array = new Byte[]{(byte) ((l >> 56) & 0xff),
                (byte) ((l >> 48) & 0xff),
                (byte) ((l >> 40) & 0xff),
                (byte) ((l >> 32) & 0xff),
                (byte) ((l >> 24) & 0xff),
                (byte) ((l >> 16) & 0xff),
                (byte) ((l >> 8) & 0xff),
                (byte) (l & 0xff)};

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
        buildPacket(true);
    }

    protected void buildPacket(boolean padXor) {
        buffer.add(0, packetType);

        if(padXor) {
            padXor();
        }

        padBuffer();

        Byte[] array = buffer.toArray(new Byte[0]);
        setData(ArrayUtils.toPrimitive(array));
    }

    // Padding needed for blowfish encryption
    private void padBuffer() {
        byte paddingLength = (byte) (buffer.size() % 8);
        if(paddingLength > 0) {

            paddingLength = (byte) (8 - paddingLength);

            //log.debug("Packet needs a padding of {} bytes", paddingLength);

            for(int i = 0; i < paddingLength; i++) {
                buffer.add((byte) 0);
            }
        }
    }

    private void padXor() {
        writeI(0);
    }
}
