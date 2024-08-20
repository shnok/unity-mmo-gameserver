package com.shnok.javaserver.dto.external.clientpackets.authentication;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.model.network.SessionKey;
import com.shnok.javaserver.thread.GameClientThread;
import com.shnok.javaserver.thread.LoginServerThread;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class AuthLoginPacket extends ClientPacket {
    // loginName + keys must match what the login server used.
    private final String account;
    private final int playKey1;
    private final int playKey2;
    private final int loginKey1;
    private final int loginKey2;

    public AuthLoginPacket(GameClientThread client, byte[] data) {
        super(client, data);

        account = readS().toLowerCase();
        playKey1 = readI();
        playKey2 = readI();
        loginKey1 = readI();
        loginKey2 = readI();
        
        handlePacket();
    }

    @Override
    public void handlePacket() {
        // avoid potential exploits
        if (client.getAccountName() == null) {
            SessionKey key = new SessionKey(getLoginKey1(), getLoginKey2(),
                    getPlayKey1(), getPlayKey2());

            log.info("Received auth request for account: {}.", getAccount());
            // Preventing duplicate login in case client login server socket was disconnected or this packet was not sent yet
            if (LoginServerThread.getInstance().addLoggedAccount(getAccount(), client)) {
                client.setAccountName(getAccount());
                LoginServerThread.getInstance().addWaitingClientAndSendRequest(getAccount(), client, key);
            } else {
                client.disconnect();
            }
        }
    }
}
