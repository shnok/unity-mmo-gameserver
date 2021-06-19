using UnityEngine;
using System;
using System.Threading;
using System.Threading.Tasks;
using System.Text;

public class ClientPacketHandler
{
    private static AsynchronousClient _client;
    
    public static void SetClient(AsynchronousClient client) {
        _client = client;
    }

    public static void SendPing() {
        PingPacket packet = new PingPacket();
        _client.SendPacket(packet);
    }

    public static void SendAuth(string username) {
       AuthPacket packet = new AuthPacket(username);
       _client.SendPacket(packet);
    }

    public static void SendMessage(string message) {
        MessagePacket packet = new MessagePacket(message);
        _client.SendPacket(packet);
    }
}
