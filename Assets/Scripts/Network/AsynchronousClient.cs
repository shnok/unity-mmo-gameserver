using UnityEngine;
using System;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Text;
using System.Collections.Generic;
using System.Collections.Concurrent;
using System.Threading.Tasks;
using System.Linq;

public class AsynchronousClient {
    private Socket client;
    private bool connected;
    private String _ipAddress;
    private int _port;
    private int _ping;
    private string _username;

    public int ping {get; set;}

    public AsynchronousClient(String ip, int port) {
        _ipAddress = ip;
        _port = port;
    }

    public bool Connect() {
        IPHostEntry ipHostInfo = Dns.GetHostEntry(_ipAddress);
        IPAddress ipAddress = ipHostInfo.AddressList[0];
        client = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

        Debug.Log("Connecting...");

        IAsyncResult result = client.BeginConnect(ipAddress, _port, null, null);

        bool success = result.AsyncWaitHandle.WaitOne(5000, true);

        if (client.Connected) {
            Debug.Log("Connection success.");
            client.EndConnect( result );
            connected = true;

            Task.Run(StartReceiving);
            return true;
        } else {
            Debug.Log("Connection failed.");
            client.Close();
            return false;
        }
    }

    public void Disconnect() {
        try {
            ServerPacketHandler.CancelTokens();

            connected = false;         
            client.Close();
            client.Dispose();
            
            GameStateManager.SetState(GameState.MENU);
        } catch (Exception e) {
            Debug.Log(e.ToString());
        }
    }

    public void SendPacket(ClientPacket packet) {
        try {
            using (NetworkStream stream = new NetworkStream(client))
            using (StreamWriter _out = new StreamWriter(stream)) {
                byte[] packetData = packet.GetData();
                char[] t = System.Text.Encoding.UTF8.GetString(packetData).ToCharArray();
                _out.Write(t, 0, t.Length);
                _out.Flush();
            }
        } catch (IOException e) {
            Debug.Log(e.ToString());
        }
    }

    public void StartReceiving() {
        using (NetworkStream stream = new NetworkStream(client))
        using (StreamReader _in = new StreamReader(stream)) {
            for(;;) {
                if(!connected) {
                    break;
                }

                int packetType = _in.Read();
                int packetLength = _in.Read();

                if (packetType == -1 || !connected) {
                    Debug.Log("Server terminated the connection.");
                    Disconnect();
                    break;
                }

                char[] c = new char[packetLength - 2];
                int receivedBytes = 0;
                int newBytes = 0;

                while ((newBytes != -1) && (receivedBytes < (packetLength - 2))) {
                    newBytes = _in.Read(c, 0, packetLength - 2);
                    receivedBytes = receivedBytes + newBytes;
                }

                byte[] packetData = Encoding.GetEncoding("UTF-8").GetBytes(c);
                ServerPacketHandler.HandlePacket((byte)packetType, packetData);
            }
        }
    }

    public void SetPing(int ping) {
        _ping = ping;
    }

    public int GetPing() {
        return _ping;
    }
}
