using UnityEngine;
using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Text;
using System.Threading.Tasks;
using System.Collections.Generic;

// State object for receiving data from remote device.
public class StateObject {
    // Client socket.
    public Socket workSocket = null;
    // Size of receive buffer.
    public const int BufferSize = 256;
    // Receive buffer.
    public byte[] buffer = new byte[BufferSize];
    // Received data string.
    public StringBuilder sb = new StringBuilder();
}

public class AsynchronousClient {

    // ManualResetEvent instances signal completion.
    private static ManualResetEvent connectDone = new ManualResetEvent(false);
    private static ManualResetEvent sendDone = new ManualResetEvent(false);
    private static ManualResetEvent receiveDone = new ManualResetEvent(false);

    // The response from the remote device.
    private static String response = String.Empty;
    private static Socket client;

    private static String _ipAddress;
    private static int _port;

    public static void setIp(String ipAddress) {
      _ipAddress = ipAddress;
    }

    public static void setPort(int port) {
      _port = port;
    }

    public static bool Connect() {
      try {
        IPHostEntry ipHostInfo = Dns.GetHostEntry(_ipAddress);
        IPAddress ipAddress = ipHostInfo.AddressList[0];
        IPEndPoint remoteEP = new IPEndPoint(ipAddress, _port);

        Debug.Log(ipHostInfo);

        // Create a TCP/IP socket.
        client = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

        // Connect to the remote endpoint.
        client.BeginConnect(remoteEP, new AsyncCallback(ConnectCallback), client);
        connectDone.WaitOne();

        return true;
      } catch (Exception e) {
        Debug.Log(e.ToString());
        return false;
      }
    }

    public static void SendPing() {
      Send(0x00, new byte[] {});
    }

    public static void SendString(String data) {
      byte[] byteData = Encoding.ASCII.GetBytes(data);
      Send(0x01, byteData);
    }

    public static void Send(byte packetType, byte[] data) {
      try {
        List<byte> packet = new List<byte>();
        packet.Add(packetType);
        packet.Add((byte)(data.Length + 2));
        packet.AddRange(data);

        byte[] byteData = packet.ToArray();

        // Begin sending the data to the remote device.
        client.BeginSend(byteData, 0, byteData.Length, 0, new AsyncCallback(SendCallback), client);

        //sendDone.WaitOne();
      } catch (Exception e) {
          Debug.Log(e.ToString());
      }
    }

    public static byte[] Receive() {
        try {
            // Create the state object.
            StateObject state = new StateObject();
            state.workSocket = client;

            // Begin receiving the data from the remote device.
            client.BeginReceive(state.buffer, 0, StateObject.BufferSize, 0, new AsyncCallback(ReceiveCallback), state);

            receiveDone.WaitOne();

            Debug.Log(response);

            return state.buffer;

        } catch (Exception e) {
            Debug.Log(e.ToString());

            return null;
        }
    }

    public static void Disconnect() {
      try {
          client.Shutdown(SocketShutdown.Both);
          client.Close();
      } catch (Exception e) {
          Debug.Log(e.ToString());
      }
    }

    private static void ConnectCallback(IAsyncResult ar) {
        try {
            // Retrieve the socket from the state object.
            Socket client = (Socket) ar.AsyncState;
            // Complete the connection.
            client.EndConnect(ar);

            Console.WriteLine("Socket connected to {0}", client.RemoteEndPoint.ToString());

            // Signal that the connection has been made.
            connectDone.Set();
        } catch (Exception e) {
            Console.WriteLine(e.ToString());
        }
    }

    private static void ReceiveCallback( IAsyncResult ar ) {
        try {
            // Retrieve the state object and the client socket
            // from the asynchronous state object.
            StateObject state = (StateObject) ar.AsyncState;
            Socket client = state.workSocket;

            // Read data from the remote device.
            int bytesRead = client.EndReceive(ar);

            if (bytesRead > 0) {
                // There might be more data, so store the data received so far.
            state.sb.Append(Encoding.ASCII.GetString(state.buffer,0,bytesRead));

                // Get the rest of the data.
                client.BeginReceive(state.buffer,0,StateObject.BufferSize, 0, new AsyncCallback(ReceiveCallback), state);
            } else {
                // All the data has arrived; put it in response.
                if (state.sb.Length > 1) {
                    response = state.sb.ToString();
                }
                // Signal that all bytes have been received.
                receiveDone.Set();
            }
        } catch (Exception e) {
            Console.WriteLine(e.ToString());
        }
    }

    private static void SendCallback(IAsyncResult ar) {
        try {
            // Retrieve the socket from the state object.
            Socket client = (Socket) ar.AsyncState;

            // Complete sending the data to the remote device.
            int bytesSent = client.EndSend(ar);
            Console.WriteLine("Sent {0} bytes to server.", bytesSent);

            // Signal that all bytes have been sent.
            sendDone.Set();
        } catch (Exception e) {
            Console.WriteLine(e.ToString());
        }
    }
}
