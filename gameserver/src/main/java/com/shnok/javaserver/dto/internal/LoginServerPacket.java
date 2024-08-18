package com.shnok.javaserver.dto.internal;

import com.shnok.javaserver.dto.ReceivablePacket;
import com.shnok.javaserver.thread.LoginServerThread;

public abstract class LoginServerPacket extends ReceivablePacket {
    protected LoginServerThread loginserver;

    public LoginServerPacket(LoginServerThread loginserver, byte[] data) {
        super(data);
        this.loginserver = loginserver;
    }
}
