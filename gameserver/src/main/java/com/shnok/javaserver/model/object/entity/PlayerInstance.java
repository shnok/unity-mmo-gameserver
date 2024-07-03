package com.shnok.javaserver.model.object.entity;

import com.shnok.javaserver.db.repository.CharacterRepository;
import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.dto.external.serverpackets.ApplyDamagePacket;
import com.shnok.javaserver.dto.external.serverpackets.UserInfoPacket;
import com.shnok.javaserver.enums.network.GameClientState;
import com.shnok.javaserver.model.PlayerAppearance;
import com.shnok.javaserver.model.item.PlayerInventory;
import com.shnok.javaserver.model.knownlist.PlayerKnownList;
import com.shnok.javaserver.model.status.PlayerStatus;
import com.shnok.javaserver.model.status.Status;
import com.shnok.javaserver.model.template.NpcTemplate;
import com.shnok.javaserver.model.template.PlayerTemplate;
import com.shnok.javaserver.service.SpawnManagerService;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Setter
public class PlayerInstance extends Entity {
    private final String name;
    private final int charId; // char id in the database
    private PlayerAppearance appearance;
    private GameClientThread gameClient;
    private PlayerInventory inventory;
    private boolean isOnline = false;

    public PlayerInstance(int charId, String name, PlayerTemplate playerTemplate) {
        this.charId = charId;
        this.name = name;
        this.template = playerTemplate;
        this.status = new PlayerStatus(playerTemplate);
    }

    @Override
    public PlayerKnownList getKnownList() {
        if ((super.getKnownList() == null) || !(super.getKnownList() instanceof PlayerKnownList)) {
            setKnownList(new PlayerKnownList(this));
        }
        return (PlayerKnownList) super.getKnownList();
    }

    // Send packet to player
    public boolean sendPacket(SendablePacket packet) {
        if(gameClient.isClientReady() && gameClient.getGameClientState() == GameClientState.IN_GAME) {
            if(gameClient.sendPacket(packet)) {
                if(packet instanceof UserInfoPacket) {
                    log.debug("[{}] Sending user packet", getGameClient().getCurrentPlayer().getId());
                }
                return true;
            }
        }

        return false;
    }

    public String getName() {
        return name;
    }

    @Override
    public void inflictDamage(Entity attacker, int value) {
        super.inflictDamage(attacker, value);

        status.setHp(Math.max(status.getHp() - value, 0));
        if (status.getHp() == 0) {
            onDeath();
        }
    }

    @Override
    public boolean onHitTimer(Entity target, int damage, boolean criticalHit) {
        if(super.onHitTimer(target, damage, criticalHit)) {
            ApplyDamagePacket applyDamagePacket = new ApplyDamagePacket(
                    getId(), target.getId(), damage, target.getStatus().getHp(), criticalHit);
            broadcastPacket(applyDamagePacket);
            sendPacket(applyDamagePacket);
            return true;
        }

        return false;
    }

    @Override
    public final PlayerStatus getStatus() {
        return (PlayerStatus) super.getStatus();
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
    public void onDeath() {

    }

    @Override
    public final PlayerTemplate getTemplate() {
        return (PlayerTemplate) super.getTemplate();
    }


    /**
     * Set the online Flag to True or False and update the characters table of the database with online status and lastAccess (called when login and logout).
     * @param isOnline
     * @param updateInDb
     */
    public void setOnlineStatus(boolean isOnline, boolean updateInDb) {
        if (this.isOnline != isOnline) {
            this.isOnline = isOnline;
        }

        // Update the characters table of the database with online status and lastAccess (called when login and logout)
        if (updateInDb) {
            CharacterRepository.getInstance().setCharacterOnlineStatus(getCharId(), isOnline);
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        setOnlineStatus(false, true);
        WorldManagerService.getInstance().removePlayer(this);
    }
}
