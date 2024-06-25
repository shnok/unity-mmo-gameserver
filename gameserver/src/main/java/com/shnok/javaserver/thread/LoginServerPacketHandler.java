package com.shnok.javaserver.thread;

import com.shnok.javaserver.dto.external.serverpackets.PingPacket;
import com.shnok.javaserver.dto.internal.loginserver.InitLSPacket;
import com.shnok.javaserver.enums.packettypes.LoginServerPacketType;
import jdk.nashorn.internal.runtime.Debug;
import lombok.extern.log4j.Log4j2;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        log.debug(Arrays.toString(data));
        loginserver.getBlowfish().decrypt(data, 0, data.length);
        log.debug(Arrays.toString(data));

        LoginServerPacketType type = LoginServerPacketType.fromByte(data[0]);

        if(type != LoginServerPacketType.Ping) {
            log.debug("Received packet from loginserver: {}", type);
        }

        switch (type) {
            case Ping:
                onReceiveEcho();
                break;
            case InitLS:
                onInitLS();
                break;
        }
    }

    private void onReceiveEcho() {
        loginserver.sendPacket(new PingPacket());

        Timer timer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (System.currentTimeMillis() - loginserver.getLastEcho() >= server.serverConnectionTimeoutMs()) {
                    log.info("Loginserver connection timeout.");
                    loginserver.disconnect();
                }
            }
        });
        timer.setRepeats(false);
        timer.start();

        loginserver.setLastEcho(System.currentTimeMillis(), timer);
    }

    private void onInitLS() {
        InitLSPacket initLSPacket = new InitLSPacket(data);
        byte[] rsaBytes = initLSPacket.getRsaKey();
        //TODO: handle initLS
    }
}
