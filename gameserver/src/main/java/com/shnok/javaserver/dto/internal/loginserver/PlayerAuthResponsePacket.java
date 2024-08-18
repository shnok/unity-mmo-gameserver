package com.shnok.javaserver.dto.internal.loginserver;

import com.shnok.javaserver.dto.external.serverpackets.CharSelectionInfoPacket;
import com.shnok.javaserver.dto.internal.LoginServerPacket;
import com.shnok.javaserver.dto.internal.gameserver.PlayerInGamePacket;
import com.shnok.javaserver.enums.network.GameClientState;
import com.shnok.javaserver.enums.network.LoginFailReason;
import com.shnok.javaserver.model.network.WaitingClient;
import com.shnok.javaserver.thread.LoginServerThread;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class PlayerAuthResponsePacket extends LoginServerPacket {
    private final String account;
    private final boolean authed;

    public PlayerAuthResponsePacket(LoginServerThread loginserver, byte[] data) {
        super(loginserver, data);

        account = readS();
        authed = readB() != 0;

        handlePacket();
    }

    @Override
    public void handlePacket() {
        String account = getAccount();
        WaitingClient wcToRemove = null;
        synchronized (loginserver.getWaitingClients()) {
            for (WaitingClient wc : loginserver.getWaitingClients()) {
                if (wc.account.equals(account)) {
                    wcToRemove = wc;
                }
            }
        }
        if (wcToRemove != null) {
            if (isAuthed()) {

                PlayerInGamePacket pig = new PlayerInGamePacket(getAccount());
                loginserver.sendPacket(pig);
                wcToRemove.gameClient.setGameClientState(GameClientState.AUTHED);
                wcToRemove.gameClient.setSessionId(wcToRemove.session);

                CharSelectionInfoPacket cl = new CharSelectionInfoPacket(
                        wcToRemove.account, wcToRemove.gameClient.getSessionId().playOkID1);
                wcToRemove.gameClient.sendPacket(cl);
                wcToRemove.gameClient.setCharSelection(cl.getCharSelectData());
            } else {
                log.warn("Session key is not correct. Closing connection for account {}.", wcToRemove.account);
                wcToRemove.gameClient.close(LoginFailReason.REASON_SYSTEM_ERROR_LOGIN_LATER);
                loginserver.getAccountsInGameServer().remove(wcToRemove.account);
            }
            loginserver.getWaitingClients().remove(wcToRemove);
        }
    }
}
