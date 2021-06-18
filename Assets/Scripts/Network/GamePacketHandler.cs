using UnityEngine;
using System;
using System.Threading;
using System.Threading.Tasks;
using System.Text;

public class GamePacketHandler
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

    public static void HandlePacket(byte packetType, byte[] data) {
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
                SendPing();
                _timestamp = DateTimeOffset.Now.ToUnixTimeMilliseconds();
            }
        }, _tokenSource.Token);

        Task.Delay(3000).ContinueWith(t => {
            if(!_tokenSource.IsCancellationRequested) {
                long now2 = DateTimeOffset.Now.ToUnixTimeMilliseconds();
                if(now2 - _timestamp > 1500) {
                    _client.Disconnect();
                }
            }
        }, _tokenSource.Token);
    }

    private static void onAuthReceive(byte[] data) {
        if(data.Length != 3) {
            _client.Disconnect();
            return;
        }

        switch(data[2]) {
            case 0x00:
                GameStateManager.SetState(GameState.CONNECTED);
                break;
            case 0x01:
                Debug.Log("User already connected.");
                _client.Disconnect();
                break;
            case 0x02:
                Debug.Log("Incorrect user name.");
                _client.Disconnect();
                break;
        }
    }

    private static void onMessageReceive(byte[] data) {
        byte senderNameLength = data[2];
        int textMessageLength = data.Length - senderNameLength - 3;
        int textMessageStartIndex = (byte)data.Length - textMessageLength;

        String sender = System.Text.Encoding.UTF8.GetString(data, 3, senderNameLength);
        String text = System.Text.Encoding.UTF8.GetString(data, textMessageStartIndex, textMessageLength);

        Debug.Log(sender + ":" + text);
        Chat.AddMessage(sender, text);
    }

    public static void SendPing() {
        _client.QueuePacket(0x00, new byte[] {});
    }

    public static void SendAuth(string username) {
        byte[] byteData = Encoding.ASCII.GetBytes(username);
        _client.QueuePacket(0x01, byteData);
    }

    public static void SendMessage(string data) {
        byte[] byteData = Encoding.ASCII.GetBytes(data);
        _client.QueuePacket(0x02, byteData);
    }
}
