package com.shnok.javaserver.dto.internal.loginserver;

import com.shnok.javaserver.dto.internal.LoginServerPacket;
import com.shnok.javaserver.enums.LoginServerFailReason;
import com.shnok.javaserver.thread.LoginServerThread;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class LoginServerFailPacket extends LoginServerPacket {
    private final int failReason;

    public LoginServerFailPacket(LoginServerThread loginserver, byte[] data) {
        super(loginserver, data);

        readB();
        readB();
        failReason = readI();
    }

    @Override
    public void handlePacket() {
        LoginServerFailReason failReason = LoginServerFailReason.fromValue(getFailReason());
        log.error("Registration Failed: {}", failReason);
    }
}
