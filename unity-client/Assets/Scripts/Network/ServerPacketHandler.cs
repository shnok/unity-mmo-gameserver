using UnityEngine;
using System;
using System.Threading;
using System.Threading.Tasks;
using System.Text;

public class ServerPacketHandler
{
    private AsynchronousClient _client;
    private long _timestamp;
    private CancellationTokenSource _tokenSource;
    private static ServerPacketHandler _instance;

    public static ServerPacketHandler GetInstance() {
        if(_instance == null) {
            _instance = new ServerPacketHandler();
        }

        return _instance;
    }
    public void SetClient(AsynchronousClient client) {
        _client = client;
        _tokenSource = new CancellationTokenSource();
    }

    public AsynchronousClient GetClient() {
        return _client;
    }

    public void HandlePacket(byte[] data) {
        byte packetType = data[0];
        switch (packetType)
        {
            case 0x00:
                onPingReceive();
                break;
            case 0x01:
                onAuthReceive(data);
                break;
            case 0x02: 
                onMessageReceive(data);
                break;
            case 0x03:
                onSystemMessageReceive(data);
                break;
            case 0x04:
                onPlayerInfoReceive(data);
                break;
            case 0x05:
                onUpdatePosition(data);
                break;
            case 0x06:
                onRemoveObject(data) ;
                break;
            case 0x07:
                onUpdateRotation(data);
                break;
            case 0x08:
                onUpdateAnimation(data);
                break;
            case 0x09:
                onInflictDamage(data);
                break;
            case 0x0A:
                onNpcInfoReceive(data);
                break;
        }
    }

    public void CancelTokens() {
        _tokenSource.Cancel();
    }

    private void onPingReceive() {
        long now = DateTimeOffset.Now.ToUnixTimeMilliseconds();
        int ping = _timestamp != 0 ? (int)(now - _timestamp) : 0;
        //Debug.Log("Ping: " + ping + "ms");
        _client.SetPing(ping);

        Task.Delay(1000).ContinueWith(t => {
            if(!_tokenSource.IsCancellationRequested) {
                ClientPacketHandler.GetInstance().SendPing();
                _timestamp = DateTimeOffset.Now.ToUnixTimeMilliseconds();
            }
        }, _tokenSource.Token);

        Task.Delay(3000).ContinueWith(t => {
            if(!_tokenSource.IsCancellationRequested) {
                long now2 = DateTimeOffset.Now.ToUnixTimeMilliseconds();
                if(now2 - _timestamp > 1500) {
                    Debug.Log("Connection timed out");
                    DefaultClient.GetInstance().Disconnect();
                }
            }
        }, _tokenSource.Token);
    }

    private void onAuthReceive(byte[] data) {
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

    private void onMessageReceive(byte[] data) {
        ReceiveMessagePacket packet = new ReceiveMessagePacket(data);
        String sender = packet.GetSender();
        String text = packet.GetText();
        Chat.AddMessage(sender, text);
    }
    private void onSystemMessageReceive(byte[] data) {
        SystemMessagePacket packet = new SystemMessagePacket(data);
        SystemMessage message = packet.GetMessage();
        Chat.AddMessage(message);
    }
    private void onPlayerInfoReceive(byte[] data) {
        PlayerInfoPacket packet = new PlayerInfoPacket(data);
        NetworkIdentity identity = packet.GetIdentity();
        PlayerStatus status = packet.GetStatus();

        World.GetInstance().SpawnPlayer(identity, status);
    }
    private void onUpdatePosition(byte[] data) {
        UpdatePositionPacket packet = new UpdatePositionPacket(data);
        int id = packet.getId();
        Vector3 position = packet.getPosition();
        World.GetInstance().UpdateObject(id, position);
    }
    private void onRemoveObject(byte[] data) {
        RemoveObjectPacket packet = new RemoveObjectPacket(data);
        World.GetInstance().RemoveObject(packet.getId());
    }

    private void onUpdateRotation(byte[] data) {
        UpdateRotationPacket packet = new UpdateRotationPacket(data);
        int id = packet.getId();
        float angle = packet.getAngle();
        World.GetInstance().UpdateObject(id, angle);
    }
    private void onUpdateAnimation(byte[] data) {
        UpdateAnimationPacket packet = new UpdateAnimationPacket(data);
        int id = packet.getId();
        int animId = packet.getAnimId();
        float value = packet.getValue();
        World.GetInstance().UpdateObject(id, animId, value);
    }
    private void onInflictDamage(byte[] data) {
        InflictDamagePacket packet = new InflictDamagePacket(data);
        World.GetInstance().InflictDamageTo(packet.SenderId, packet.TargetId, packet.AttackId, packet.Value);
    }
    private void onNpcInfoReceive(byte[] data) {
        NpcInfoPacket packet = new NpcInfoPacket(data);
        NetworkIdentity identity = packet.GetIdentity();
        NpcStatus status = packet.GetStatus();

        World.GetInstance().SpawnNpc(identity, status);
    }
}
