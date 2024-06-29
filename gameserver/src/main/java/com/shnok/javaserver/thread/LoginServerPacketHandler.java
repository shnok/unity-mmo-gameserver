package com.shnok.javaserver.thread;

import com.shnok.javaserver.db.entity.DBCharacter;
import com.shnok.javaserver.dto.internal.gameserver.*;
import com.shnok.javaserver.dto.internal.loginserver.AuthResponsePacket;
import com.shnok.javaserver.dto.internal.loginserver.InitLSPacket;
import com.shnok.javaserver.dto.internal.loginserver.LoginServerFailPacket;
import com.shnok.javaserver.dto.internal.loginserver.RequestCharactersPacket;
import com.shnok.javaserver.enums.LoginServerFailReason;
import com.shnok.javaserver.enums.packettypes.LoginServerPacketType;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.security.NewCrypt;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.service.db.PlayerTable;
import com.shnok.javaserver.util.HexUtils;
import com.shnok.javaserver.util.ServerNameDAO;
import lombok.extern.log4j.Log4j2;

import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        if(!NewCrypt.verifyChecksum(data)) {
            log.warn("Packet's checksum is wrong.");
            return;
        }

        LoginServerPacketType type = LoginServerPacketType.fromByte(data[0]);

        log.debug("Received packet from loginserver: {}", type);

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

        loginserver.sendPacket(new AuthRequestPacket(loginserver.getRequestID(), loginserver.isAcceptAlternate(),
                loginserver.getHexID(), loginserver.getPort(), loginserver.getMaxPlayer(),
                loginserver.getSubnets(), loginserver.getHosts()));

    }

    private void onLoginAuthFail() {
        LoginServerFailPacket packet = new LoginServerFailPacket(data);
        LoginServerFailReason failReason = LoginServerFailReason.fromValue(packet.getFailReason());
        log.error("Registration Failed: {}", failReason);
    }

    private void onAuthResponse() {
        // Handle auth response
        AuthResponsePacket packet = new AuthResponsePacket(data);
        int serverId = packet.getId();
        String serverName = ServerNameDAO.getServer(serverId);

        loginserver.saveHexId(serverId, HexUtils.bytesToHex(loginserver.getHexID()));

        log.info("Registered on login as Server {}: {}", serverId, serverName);

        // Share status
        ServerStatusPacket statusPacket = new ServerStatusPacket();

        if(server.serverGMOnly()) {
            statusPacket.addAttribute(ServerStatusPacket.SERVER_LIST_STATUS, ServerStatusPacket.STATUS_GM_ONLY);
        } else {
            statusPacket.addAttribute(ServerStatusPacket.SERVER_LIST_STATUS, ServerStatusPacket.STATUS_LIGHT);
        }

        statusPacket.addAttribute(ServerStatusPacket.MAX_PLAYERS, loginserver.getMaxPlayer());

        statusPacket.build();

        loginserver.sendPacket(statusPacket);

        // Share logged in usersAD
        if(WorldManagerService.getInstance().getAllPlayers().size() > 0) {
            List<String> playerList = new ArrayList<>();

            for(PlayerInstance player : WorldManagerService.getInstance().getAllPlayers().values()) {
                playerList.add(player.getName());
                //TODO: change to accountname
            }

            loginserver.sendPacket(new PlayerInGamePacket(playerList));
        }
    }

    private void onRequestCharacters() {
        RequestCharactersPacket packet = new RequestCharactersPacket(data);

        String account = packet.getAccount().toLowerCase();

        List<DBCharacter> characters = PlayerTable.getInstance().getCharactersForAccount(account);

        log.info("Account {} have {} character(s).", account, characters.size());

        if(characters.size() == 0 && server.createRandomCharacter()) {

            PlayerTable.getInstance().createRandomCharForAccount(account);

            characters = PlayerTable.getInstance().getCharactersForAccount(account);

            log.info("Account {} have {} character(s).", account, characters.size());
        }

        loginserver.sendPacket(new ReplyCharactersPacket(account, characters.size()));
    }
}
