using UnityEngine;
using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Text;
using System.Collections.Generic;
using System.Collections.Concurrent;
using System.Threading.Tasks;

public class StateObject {
    public Socket workSocket = null;
    public const int BufferSize = 256;
    public byte[] buffer = new byte[BufferSize];
    public StringBuilder sb = new StringBuilder();
}

public class AsynchronousClient {
    private ManualResetEvent sendDone = new ManualResetEvent(false);
    private ManualResetEvent receiveDone = new ManualResetEvent(false);
    private BlockingCollection<byte[]> _sendQueue = new BlockingCollection<byte[]>();
    private Socket client;
    private String response = String.Empty;
    private bool connected;
    private String _ipAddress;
    private int _port;
    private int _ping;

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
            Task.Run(StartSending);
            return true;
        } else {
            Debug.Log("Connection failed.");
            client.Close();
            return false;
        }
    }

    public void Disconnect() {
        try {
            connected = false;
            receiveDone.Set();
            sendDone.Set();

            
            client.Close();
            client.Dispose();
            
            GameStateManager.SetState(GameState.MENU);
        } catch (Exception e) {
            Debug.Log(e.ToString());
        }
    }

    public void QueuePacket(byte packetType, byte[] data) {
        List<byte> packet = new List<byte>();
        packet.Add(packetType);
        packet.Add((byte)(data.Length + 2));
        packet.AddRange(data);

        _sendQueue.Add(packet.ToArray());
    }

    public void StartSending() {
        for(;;) {
            if(!connected) {
                break;
            }

            while (!_sendQueue.IsCompleted) {
                byte[] byteData = null;

                try {
                    byteData = _sendQueue.Take();
                } catch (InvalidOperationException) { }

                if (byteData != null) {
                    Send(byteData);
                }
            }
        }
    }

    public void Send(byte[] byteData) {
        try {
            if(!connected) {
                return;
            }

            client.BeginSend(byteData, 0, byteData.Length, 0, new AsyncCallback(SendCallback), client);
            sendDone.WaitOne();
            //Debug.Log("Sent ["+string.Join(", ", byteData)+"]");
        } catch (Exception e) {
            Debug.Log(e.ToString());
        }
    }

    public void StartReceiving() {
        for(;;) {
            if(!connected) {
                break;
            }

            try {
                StateObject state = new StateObject();
                state.workSocket = client;

                // Begin receiving the data from the remote device.
                client.BeginReceive(state.buffer, 0, StateObject.BufferSize, 0, new AsyncCallback(ReceiveCallback), state);
                receiveDone.WaitOne();
                byte packetType = (byte)(state.buffer[0] & 0xff);

                //Debug.Log("Received: ["+string.Join(", ", state.buffer)+"]");
                GamePacketHandler.HandlePacket(packetType, state.buffer);
                //Receive();
            } catch (Exception e) {
                Debug.Log(e.ToString());
            }
        }
    }

    private void ReceiveCallback( IAsyncResult ar ) {
        try {
            StateObject state = (StateObject) ar.AsyncState;
            Socket client = state.workSocket;

            // Read data from the remote device.
            int bytesRead = client.EndReceive(ar);

            if (bytesRead > 0) {
                state.sb.Append(Encoding.ASCII.GetString(state.buffer, 0, bytesRead));
            }

            int bytesRemain = state.workSocket.Available;
            if (bytesRemain > 0) {
                state.sb.Append(Encoding.ASCII.GetString(state.buffer,0,bytesRead));
                client.BeginReceive(state.buffer,0, StateObject.BufferSize, 0, new AsyncCallback(ReceiveCallback), state);
            } else {
                if (state.sb.Length > 1) {
                    response = state.sb.ToString();
                }
                receiveDone.Set();
                receiveDone.Reset();
            }
        } catch (Exception e) {
            Console.WriteLine(e.ToString());
        }
    }

    private void SendCallback(IAsyncResult ar) {
        try {
            Socket client = (Socket) ar.AsyncState;
            int bytesSent = client.EndSend(ar);

            sendDone.Set();
            sendDone.Reset();
        } catch (Exception e) {
            Console.WriteLine(e.ToString());
        }
    }

    public void SetPing(int ping) {
        _ping = ping;
    }

    public int GetPing() {
        return _ping;
    }
}
