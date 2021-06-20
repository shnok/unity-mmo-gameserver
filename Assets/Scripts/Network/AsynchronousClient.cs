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
        Debug.Log((new System.Diagnostics.StackTrace()).GetFrame(1).GetMethod().Name);
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

                // TBD
                char[] t = System.Text.Encoding.UTF8.GetString(packetData).ToCharArray();
                _out.Write(t, 0, t.Length);
                _out.Flush();
            }
        } catch (IOException e) {
            Debug.Log(e.ToString());
        }
    }
    public void StartReceiving() {
        using (NetworkStream stream = new NetworkStream(client)) {
            for(;;) {
                if(!connected) {
                    break;
                }
   
                byte[] head = new byte[2];
                stream.Read(head, 0, 2);
                int packetType = head[0];
                int packetLength = head[1];

                if (packetType == -1 || !connected) {
                    Debug.Log("Server terminated the connection.");
                    Disconnect();
                    break;
                }

                byte[] tail = new byte[packetLength - 2];
                int received = 0;
                int readCount = 0;

                
                while ((readCount != -1) && (received < tail.Length)) {
                    readCount = stream.Read(tail, 0, tail.Length);
                    received += readCount;
                }

                ServerPacketHandler.HandlePacket((byte)packetType, tail);
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
