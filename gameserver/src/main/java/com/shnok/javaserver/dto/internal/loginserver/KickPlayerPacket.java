package com.shnok.javaserver.dto.internal.loginserver;

import com.shnok.javaserver.dto.internal.LoginServerPacket;
import com.shnok.javaserver.thread.LoginServerThread;
import lombok.Getter;

@Getter
public class KickPlayerPacket extends LoginServerPacket {
    private final String account;

    public KickPlayerPacket(LoginServerThread loginserver, byte[] data) {
        super(loginserver, data);

        account = readS();

        handlePacket();
    }

    @Override
    public void handlePacket() {
        loginserver.kickPlayer(getAccount());
    }
}
