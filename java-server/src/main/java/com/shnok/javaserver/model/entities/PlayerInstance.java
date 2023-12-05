package com.shnok.javaserver.model.entities;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.model.knownlist.PlayerKnownList;
import com.shnok.javaserver.model.status.PlayerStatus;
import com.shnok.javaserver.model.status.Status;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Setter
public class PlayerInstance extends Entity {
    public final String name;
    private PlayerStatus status;
    private GameClientThread gameClient;

    public PlayerInstance(int id, String name) {
        super(id);
        this.name = name;
    }

    @Override
    public PlayerKnownList getKnownList() {
        if ((super.getKnownList() == null) || !(super.getKnownList() instanceof PlayerKnownList)) {
            setKnownList(new PlayerKnownList(this));
        }
        return (PlayerKnownList) super.getKnownList();
    }

    // Send packet to player
    public void sendPacket(ServerPacket packet) {
        gameClient.sendPacket(packet);
    }

    public String getName() {
        return name;
    }

    @Override
    public void inflictDamage(int value) {
        status.setHp(status.getHp() - value);
    }

    @Override
    public PlayerStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = (PlayerStatus) status;
    }

    @Override
    public boolean canMove() {
        return canMove;
    }

    @Override
    public boolean moveTo(int x, int y, int z) {
        return false;
    }

    @Override
    public void onDeath() {

    }
}
