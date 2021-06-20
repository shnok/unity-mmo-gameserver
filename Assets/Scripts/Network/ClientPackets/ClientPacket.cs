using System;
using UnityEngine;

public abstract class ClientPacket : Packet {

    public ClientPacket(byte type) : base(type) {}
    public ClientPacket(byte[] data) : base(data) {
        BuildPacket(data);
    }

    public void BuildPacket() {
        if(segments.Count == 0) {
            return;
        }

        int totalSize = 0;
        foreach (byte[] b in segments) {
            totalSize += b.Length;
        }

        _packetLength = (byte)(totalSize + 2);
        byte[] data = new byte[_packetLength];
        data[0] = _packetType;
        data[1] = _packetLength;

        int index = 2;
        foreach (byte[] s in segments) {
            Array.Copy(s, 0, data, index, s.Length);
            index += s.Length;
        }

        _packetData = data;

        Debug.Log("Sent: [" + string.Join(",", _packetData) + "]");
    }

    public void BuildPacket(byte[] data) {
        _packetLength = (byte)(data.Length + 2);
        _packetData = new byte[_packetLength];
        _packetData[0] = _packetType;
        _packetData[1] = _packetLength;
        for (int i = 2; i < data.Length + 2; i++) {
            _packetData[i] = data[i - 2];
        }
    }
}