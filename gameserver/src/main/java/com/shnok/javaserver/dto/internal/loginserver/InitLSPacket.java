package com.shnok.javaserver.dto.internal.loginserver;

import com.shnok.javaserver.dto.internal.LoginServerPacket;
import com.shnok.javaserver.dto.internal.gameserver.AuthRequestPacket;
import com.shnok.javaserver.dto.internal.gameserver.BlowFishKeyPacket;
import com.shnok.javaserver.security.NewCrypt;
import com.shnok.javaserver.thread.LoginServerThread;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

@Log4j2
@Getter
public class InitLSPacket extends LoginServerPacket {
    private final byte[] rsaKey;

    public InitLSPacket(LoginServerThread loginserver, byte[] data) {
        super(loginserver, data);

        readB();
        int rsaKeyLength = readI();
        rsaKey = readB(rsaKeyLength);

        handlePacket();
    }

    @Override
    public void handlePacket() {
        byte[] rsaBytes = getRsaKey();

        log.debug("Received RSA public key [{}]: {}", rsaBytes.length, Arrays.toString(rsaBytes));

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

        loginserver.sendPacket(new AuthRequestPacket(loginserver.getRequestID(), loginserver.isAcceptAlternate(),
                loginserver.getHexID(), loginserver.getGamePort(), loginserver.getMaxPlayer(),
                loginserver.getSubnets(), loginserver.getHosts()));
    }
}
