package com.shnok.javaserver.dto.internal.gameserver;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.internal.GameServerPacketType;
import lombok.extern.log4j.Log4j2;

import javax.crypto.Cipher;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
public class BlowFishKeyPacket extends SendablePacket {
    public BlowFishKeyPacket(byte[] blowfishKey, RSAPublicKey publicKey) {
        super(GameServerPacketType.BlowFishKey.getValue());

        try {
            if(server.printCryptography()) {
                log.debug("Decrypted blowfish key [{}]: {}", blowfishKey.length, Arrays.toString(blowfishKey));
            }

            final Cipher rsaCipher = Cipher.getInstance(server.rsaPaddingMode());
            rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = rsaCipher.doFinal(blowfishKey);
            writeB((byte) 0);
            writeB((byte) 0);
            writeI(encrypted.length);
            writeB(encrypted);

            if(server.printCryptography()) {
                log.debug("Encrypted blowfish key [{}]: {}", encrypted.length, Arrays.toString(encrypted));
            }
            buildPacket();
        } catch (Exception e) {
            log.error("Error While encrypting blowfish key for transmision (Crypt error): " + e.getMessage(), e);
        }
    }
}
