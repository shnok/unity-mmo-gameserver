using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;

public abstract class ServerPacket : Packet
{
    protected byte minimumLength;

    public ServerPacket() {}
    public ServerPacket(byte[] d) : base(d) {
        ParseData();
    }

    public void ParseData() {
        for(int i = 0; i < _packetData.Length; i++) {
            int nextSegment = (int)_packetData[i];
            if((nextSegment + 1) <= _packetData.Length) {
                byte[] segment = new byte[nextSegment];
                Array.Copy(_packetData, i + 1, segment, 0, nextSegment);
                segments.Add(segment);  
            } else {
                Debug.Log("Error in packet data segments.");
                return;
            }
            i += nextSegment;
        }

        _packetLength = (byte)_packetData.Length;
    }

    public abstract void Parse();
}
