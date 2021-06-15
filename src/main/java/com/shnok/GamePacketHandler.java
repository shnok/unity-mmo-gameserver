package com.shnok;

public class GamePacketHandler {

    private GameClient _client;

    public GamePacketHandler(GameClient client) {
        _client = client;
    }

    public void handle(byte type, byte[] data) {
        switch (type) {
            case 0x00:
                onReceiveEcho();
                break;
            case 0x01:
                onReceiveString(data);
                break;
        }
    }

    private void onReceiveEcho() {
        _client.sendPacket(new byte[] { 0x00, 0x02} );
    }

    private void onReceiveString(byte[] data) {
        String value = new String(data);
        System.out.println("Received string: " + value);
    }
}
