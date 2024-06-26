package com.shnok.javaserver.thread;

import com.shnok.javaserver.dto.external.serverpackets.PingPacket;
import com.shnok.javaserver.dto.internal.gameserver.AuthRequest;
import com.shnok.javaserver.dto.internal.gameserver.BlowFishKeyPacket;
import com.shnok.javaserver.dto.internal.loginserver.InitLSPacket;
import com.shnok.javaserver.enums.packettypes.LoginServerPacketType;
import com.shnok.javaserver.security.NewCrypt;
import jdk.nashorn.internal.runtime.Debug;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

import static com.shnok.javaserver.config.Configuration.server;

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
        log.debug("<--- Encrypted packet {} : {}", data.length, Arrays.toString(data));
        loginserver.getBlowfish().decrypt(data, 0, data.length);
        log.debug("<--- Decrypted packet {} : {}", data.length, Arrays.toString(data));

        LoginServerPacketType type = LoginServerPacketType.fromByte(data[0]);

        log.debug("Received packet from loginserver: {}", type);

        switch (type) {
            case InitLS:
                onInitLS();
                break;
                //TODO: Handle fail packet
        }
    }

    private void onInitLS() {
        InitLSPacket initLSPacket = new InitLSPacket(data);
        byte[] rsaBytes = initLSPacket.getRsaKey();

        log.debug("Received RSA public key {} - {}", rsaBytes.length, Arrays.toString(rsaBytes));

        RSAPublicKey publicKey;
        try {
            KeyFactory kfac = KeyFactory.getInstance("RSA");
            BigInteger modulus = new BigInteger(rsaBytes);
            RSAPublicKeySpec kspec1 = new RSAPublicKeySpec(modulus, RSAKeyGenParameterSpec.F4);
            publicKey = (RSAPublicKey) kfac.generatePublic(kspec1);
        } catch (GeneralSecurityException e) {
            log.warn("Trouble while init the public key send by login");
            return;
        }

        loginserver.sendPacket(new BlowFishKeyPacket(loginserver.getBlowfishKey(), publicKey));
        loginserver.setBlowfish(new NewCrypt(loginserver.getBlowfishKey()));

        log.info("Updated loginserver blowfish");

        loginserver.sendPacket(new AuthRequest(loginserver.getRequestID(), loginserver.isAcceptAlternate(),
                loginserver.getHexID(), loginserver.getPort(), loginserver.getMaxPlayer(),
                loginserver.getSubnets(), loginserver.getHosts()));

    }
}
