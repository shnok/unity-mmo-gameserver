package com.shnok.javaserver.thread;

import com.shnok.javaserver.dto.internal.loginserver.*;
import com.shnok.javaserver.enums.network.packettypes.internal.LoginServerPacketType;
import com.shnok.javaserver.security.NewCrypt;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

@Log4j2
public class LoginServerPacketHandler extends Thread {
    private final LoginServerThread loginserver;
    private final byte[] data;

    public LoginServerPacketHandler(LoginServerThread loginserver, byte[] data) {
        this.loginserver = loginserver;
        this.data = data;
    }

    @Override
    public void run() {
        handle();
    }

    public void handle() {
        if(loginserver.isPrintCryptography()) {
            log.debug("<--- [LOGIN] Encrypted packet {} : {}", data.length, Arrays.toString(data));
        }
        loginserver.getBlowfish().decrypt(data, 0, data.length);
        if(loginserver.isPrintCryptography()) {
            log.debug("<--- [LOGIN] Decrypted packet {} : {}", data.length, Arrays.toString(data));
        }

        if(!NewCrypt.verifyChecksum(data)) {
            log.warn("Packet's checksum is wrong.");
            return;
        }

        LoginServerPacketType type = LoginServerPacketType.fromByte(data[0]);

        if(loginserver.isPrintPacketsIn()) {
            log.debug("[LOGIN] Received packet: {}", type);
        }

        switch (type) {
            case InitLS:
                onInitLS();
                break;
            case Fail:
                onLoginAuthFail();
                break;
            case AuthResponse:
                onAuthResponse();
                break;
            case RequestCharacters:
                onRequestCharacters();
                break;
            case PlayerAuthResponse:
                onPlayerAuthResponse();
                break;
            case KickPlayer:
                onKickPlayer();
                break;
        }
    }

    private void onInitLS() {
        InitLSPacket initLSPacket = new InitLSPacket(loginserver, data);
    }

    private void onLoginAuthFail() {
        LoginServerFailPacket packet = new LoginServerFailPacket(loginserver, data);
    }

    private void onAuthResponse() {
        // Handle auth response
        AuthResponsePacket packet = new AuthResponsePacket(loginserver, data);
    }

    private void onRequestCharacters() {
        RequestCharactersPacket packet = new RequestCharactersPacket(loginserver, data);
    }

    private void onPlayerAuthResponse() {
        PlayerAuthResponsePacket packet = new PlayerAuthResponsePacket(loginserver, data);
    }

    private void onKickPlayer() {
        KickPlayerPacket packet = new KickPlayerPacket(loginserver, data);
    }
}
