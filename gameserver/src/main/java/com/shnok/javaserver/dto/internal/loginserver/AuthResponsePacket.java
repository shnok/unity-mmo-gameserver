package com.shnok.javaserver.dto.internal.loginserver;

import com.shnok.javaserver.dto.internal.LoginServerPacket;
import com.shnok.javaserver.dto.internal.gameserver.PlayerInGamePacket;
import com.shnok.javaserver.dto.internal.gameserver.ServerStatusPacket;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.thread.LoginServerThread;
import com.shnok.javaserver.util.HexUtils;
import com.shnok.javaserver.util.ServerNameDAO;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
@Getter
public class AuthResponsePacket extends LoginServerPacket {
    private final int id;

    public AuthResponsePacket(LoginServerThread loginserver, byte[] data) {
        super(loginserver, data);

        readB();
        readB();
        id = readI();

        handlePacket();
    }

    @Override
    public void handlePacket() {
        int serverId = getId();
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

        // Share logged in users
        if(!WorldManagerService.getInstance().getAllPlayers().isEmpty()) {
            List<String> playerList = new ArrayList<>();

            for(PlayerInstance player : WorldManagerService.getInstance().getAllPlayers().values()) {
                playerList.add(player.getGameClient().getAccountName());
            }

            loginserver.sendPacket(new PlayerInGamePacket(playerList));
        }
    }
}