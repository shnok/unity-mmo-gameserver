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
        }
    }

    public static void CancelTokens() {
        _tokenSource.Cancel();
    }

    private static void onPingReceive() {
        long now = DateTimeOffset.Now.ToUnixTimeMilliseconds();
        int ping = _timestamp != 0 ? (int)(now - _timestamp) : 0;
        Debug.Log("Ping: " + ping + "ms");
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

    public static void SendPing() {
        _client.QueuePacket(0x00, new byte[] {});
    }

    public static void SendString(string data) {
        byte[] byteData = Encoding.ASCII.GetBytes(data);
        _client.QueuePacket(0x01, byteData);
    }
}
