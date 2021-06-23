using UnityEngine;
using System;
using System.Threading;
using System.Threading.Tasks;
using System.Text;

public class ClientPacketHandler
{
    private static ClientPacketHandler _instance;

    public static ClientPacketHandler GetInstance() {
        if(_instance == null) {
            _instance = new ClientPacketHandler();
        }

        return _instance;
    }

    private AsynchronousClient _client;
    
    public void SetClient(AsynchronousClient client) {
        _client = client;
    }

    public void SendPing() {
        PingPacket packet = new PingPacket();
        _client.SendPacket(packet);
    }

    public void SendAuth(string username) {
       AuthRequestPacket packet = new AuthRequestPacket(username);
       _client.SendPacket(packet);
    }

    public void SendMessage(string message) {
        SendMessagePacket packet = new SendMessagePacket(message);
        _client.SendPacket(packet);
    }

    public void UpdatePosition(Vector3 position) {
        RequestMovePacket packet = new RequestMovePacket(position);
        _client.SendPacket(packet);
    }

    public void SendLoadWorld() {
        LoadWorldPacket packet = new LoadWorldPacket();
        _client.SendPacket(packet);
    }
}
