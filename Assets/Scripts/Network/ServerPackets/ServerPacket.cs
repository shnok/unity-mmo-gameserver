using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using System.Text;

public abstract class ServerPacket : Packet
{
    protected byte minimumLength;
    private int iterator;

    public ServerPacket() {}
    public ServerPacket(byte[] d) : base(d) {
        ReadB();
        ReadB();
    }

    protected byte ReadB() {
        return _packetData[iterator++];
    }

    protected string ReadS() {
        byte strLen = ReadB();
        byte[] data = new byte[strLen];
        Array.Copy(_packetData, iterator, data, 0, strLen);
        iterator += strLen;

        return Encoding.GetEncoding("UTF-8").GetString(data);
    }

    public abstract void Parse();
}
