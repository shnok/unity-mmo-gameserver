using System.Collections;
using System.Collections.Generic;
using System;
using UnityEngine;

public abstract class Packet {
    protected List<byte[]> segments = new List<byte[]>();
    protected byte[] _packetData;
    protected byte _packetLength;
    protected byte _packetType;

    public Packet() {}
    public Packet(byte type) {
        _packetType = type;
    }

    public Packet(byte[] d) {
        _packetData = d;
    }

    public void SetData(byte[] data) {
        _packetType = (byte)(data.Length);
        _packetLength = (byte)(data.Length);
        _packetData = data;
    }

    public byte[] GetData() {
        return _packetData;
    }
 
    public byte GetPacketType() {
        return _packetType;
    }

    public byte GetLength() {
        return _packetLength;
    }

    public void WriteB(byte b) {
        Write(new byte[] {b});
    }

    public void WriteS(String s) {
        Write(System.Text.Encoding.GetEncoding("UTF-8").GetBytes(s)); 
    }

    private void Write(byte[] data) {
        byte[] res = new byte[data.Length + 1];
        res[0] = (byte)data.Length;
        Array.Copy(data, 0, res, 1, data.Length);
        segments.Add(res);
    }

    protected byte ReadB(int index) {
        return segments[index][0];
    }

    protected string ReadS(int index) {
        return System.Text.Encoding.GetEncoding("UTF-8").GetString(segments[index]);
    }
}