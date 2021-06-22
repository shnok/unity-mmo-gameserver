using UnityEngine;
using System;
using System.Threading;
using System.Threading.Tasks;
using System.Text;

public class ServerPacketHandler
{
    private static AsynchronousClient _client;
    private static long _timestamp;
    private static CancellationTokenSource _tokenSource;
    public static void SetClient(AsynchronousClient client) {
        _client = client;
        _tokenSource = new CancellationTokenSource();
    }

    public static AsynchronousClient GetClient() {
        return _client;
    }

    public static void HandlePacket(byte[] data) {
        byte packetType = data[0];
        switch (packetType)
        {
            case 00:
                onPingReceive();
                break;
            case 01:
                onAuthReceive(data);
                break;
            case 02: 
                onMessageReceive(data);
                break;
            case 03:
                onSystemMessageReceive(data);
                break;
            case 04:
                onPlayerInfoReceive(data);
                break;
        }
    }

    public static void CancelTokens() {
        _tokenSource.Cancel();
    }

    private static void onPingReceive() {
        long now = DateTimeOffset.Now.ToUnixTimeMilliseconds();
        int ping = _timestamp != 0 ? (int)(now - _timestamp) : 0;
        //Debug.Log("Ping: " + ping + "ms");
        _client.SetPing(ping);

        Task.Delay(1000).ContinueWith(t => {
            if(!_tokenSource.IsCancellationRequested) {
                ClientPacketHandler.SendPing();
                _timestamp = DateTimeOffset.Now.ToUnixTimeMilliseconds();
            }
        }, _tokenSource.Token);

        Task.Delay(3000).ContinueWith(t => {
            if(!_tokenSource.IsCancellationRequested) {
                long now2 = DateTimeOffset.Now.ToUnixTimeMilliseconds();
                if(now2 - _timestamp > 1500) {
                    Debug.Log("Connection timed out");
                    _client.Disconnect();
                }
            }
        }, _tokenSource.Token);
    }

    private static void onAuthReceive(byte[] data) {
        AuthResponsePacket packet = new AuthResponsePacket(data);
        AuthResponse response = packet.GetResponse();

        switch(response) {
            case AuthResponse.ALLOW:
                GameStateManager.SetState(GameState.CONNECTED);
                break;
            case AuthResponse.ALREADY_CONNECTED:
                Debug.Log("User already connected.");
                _client.Disconnect();
                break;
            case AuthResponse.INVALID_USERNAME:
                Debug.Log("Incorrect user name.");
                _client.Disconnect();
                break;
        }
    }

    private static void onMessageReceive(byte[] data) {
        //Debug.Log("Received: [" + string.Join(",", data) + "]");

        ReceiveMessagePacket packet = new ReceiveMessagePacket(data);
        String sender = packet.GetSender();
        String text = packet.GetText();
        Chat.AddMessage(sender, text);
    }

    private static void onSystemMessageReceive(byte[] data) {
        SystemMessagePacket packet = new SystemMessagePacket(data);
        SystemMessage message = packet.GetMessage();
        Chat.AddMessage(message);
    }

    private static void onPlayerInfoReceive(byte[] data) {
        PlayerInfoPacket packet = new PlayerInfoPacket(data);
        NetworkIdentity identity = packet.GetIdentity();
        PlayerStatus status = packet.GetStatus();

        World.GetInstance().SpawnPlayer(identity, status);

        //DefaultClient.GetInstance().NewPlayer(player);
        
        //if(player.GetName() == DefaultClient.GetInstance().)
    }
}
