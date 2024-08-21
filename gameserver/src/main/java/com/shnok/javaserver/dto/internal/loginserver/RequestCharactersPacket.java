package com.shnok.javaserver.dto.internal.loginserver;

import com.shnok.javaserver.db.entity.DBCharacter;
import com.shnok.javaserver.db.repository.CharacterRepository;
import com.shnok.javaserver.dto.internal.LoginServerPacket;
import com.shnok.javaserver.dto.internal.gameserver.ReplyCharactersPacket;
import com.shnok.javaserver.thread.LoginServerThread;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
@Getter
public class RequestCharactersPacket extends LoginServerPacket {
    private final String account;

    public RequestCharactersPacket(LoginServerThread loginserver, byte[] data) {
        super(loginserver, data);

        account = readS();

        handlePacket();
    }

    @Override
    public void handlePacket() {
        String account = getAccount().toLowerCase();

        List<DBCharacter> characters = CharacterRepository.getInstance().getCharactersForAccount(account);

        log.info("Account {} have {} character(s).", account, characters.size());

        if(characters.isEmpty() && server.createRandomCharacter()) {
            for(int i = 0; i < 3; i ++) {
                CharacterRepository.getInstance().createRandomCharForAccount(account);
            }

            characters = CharacterRepository.getInstance().getCharactersForAccount(account);

            log.info("Account {} have {} character(s).", account, characters.size());
        }

        loginserver.sendPacket(new ReplyCharactersPacket(account, characters.size()));
    }
}
