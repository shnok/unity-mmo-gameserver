package com.shnok.javaserver.model.object.entity;

import com.shnok.javaserver.db.entity.DBArmor;
import com.shnok.javaserver.db.entity.DBWeapon;
import com.shnok.javaserver.db.repository.CharacterRepository;
import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.dto.external.serverpackets.ApplyDamagePacket;
import com.shnok.javaserver.dto.external.serverpackets.StatusUpdatePacket;
import com.shnok.javaserver.dto.external.serverpackets.SystemMessagePacket;
import com.shnok.javaserver.dto.external.serverpackets.UserInfoPacket;
import com.shnok.javaserver.enums.item.ItemSlot;
import com.shnok.javaserver.enums.network.GameClientState;
import com.shnok.javaserver.enums.network.SystemMessageId;
import com.shnok.javaserver.model.Party;
import com.shnok.javaserver.model.PlayerAppearance;
import com.shnok.javaserver.model.item.PlayerInventory;
import com.shnok.javaserver.model.knownlist.PlayerKnownList;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.stats.Formulas;
import com.shnok.javaserver.model.stats.PlayerStat;
import com.shnok.javaserver.model.status.PlayerStatus;
import com.shnok.javaserver.model.template.PlayerTemplate;
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
    private boolean sitting;
    private boolean isOnline = false;
    private boolean GM = false;
    private Party party;

    public PlayerInstance(int id, int charId, String name, PlayerTemplate playerTemplate) {
        super(id, playerTemplate);

        this.charId = charId;
        this.name = name;
        this.status = new PlayerStatus(this);

        Formulas.addFuncsToNewPlayer(this);
    }

    @Override
    public PlayerKnownList getKnownList() {
        if ((super.getKnownList() == null) || !(super.getKnownList() instanceof PlayerKnownList)) {
            setKnownList(new PlayerKnownList(this));
        }
        return (PlayerKnownList) super.getKnownList();
    }

    @Override
    public DBWeapon getActiveWeaponItem() {
        return inventory.getEquippedWeapon();
    }

    @Override
    public DBArmor getSecondaryWeaponItem() {
        return inventory.getEquippedSecondaryWeapon();
    }

    // Send packet to player
    @Override
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

    public boolean sendPacket(SystemMessageId systemMessageId) {
        SystemMessagePacket packet = new SystemMessagePacket(systemMessageId);
        if(gameClient.isClientReady() && gameClient.getGameClientState() == GameClientState.IN_GAME) {
            if(gameClient.sendPacket(packet)) {
                return true;
            }
        }

        return false;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean onHitTimer(Entity target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld) {
        if(super.onHitTimer(target, damage, crit, miss, soulshot, shld)) {
            ApplyDamagePacket applyDamagePacket = new ApplyDamagePacket(
                    getId(), target.getId(), damage, target.getStatus().getCurrentHp(), crit);
            broadcastPacket(applyDamagePacket);
            sendPacket(applyDamagePacket);

            return true;
        }

        return false;
    }

//    @Override
//    public final PlayerStatus getStatus() {
//        return (PlayerStatus) super.getStatus();
//    }
//
//    @Override
//    public void setStatus(Status status) {
//        this.status = (PlayerStatus) status;
//    }

    @Override
    public PlayerStat getStat() {
        return (PlayerStat) super.getStat();
    }

    @Override
    public void initCharStat() {
        setStat(new PlayerStat(this));
    }

    @Override
    public ItemInstance getActiveWeaponInstance() {
        return inventory.getEquippedItem(ItemSlot.rhand);
    }

    @Override
    public ItemInstance getSecondaryWeaponInstance() {
        return inventory.getEquippedItem(ItemSlot.lhand);
    }

    @Override
    public PlayerStatus getStatus() {
        return (PlayerStatus) super.getStatus();
    }

    @Override
    public void initCharStatus() {
        setStatus(new PlayerStatus(this));
    }

    @Override
    public boolean canMove() {
        return canMove;
    }

    @Override
    public void doDie(Entity attacker) {
        super.doDie(attacker);
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

        stopAllTimers();
        setOnlineStatus(false, true);
        WorldManagerService.getInstance().removePlayer(this);
    }

    @Override
    public float getCurrentCp() {
        return status.getCurrentCp();
    }

    @Override
    public void setCurrentCp(int cp, boolean broadcast) {
        ((PlayerStatus) status).setCurrentCp(cp, broadcast);
    }

    public boolean isSitting() {
        return false;
    }

    public void standUp() {

    }

    @Override
    public void broadcastStatusUpdate() {
        //System.out.println("broadcastStatusUpdate");
        // Send the Server->Client packet StatusUpdate with current HP, MP and CP to this L2PcInstance
        StatusUpdatePacket su = new StatusUpdatePacket(this);
        su.addAttribute(StatusUpdatePacket.MAX_HP, getMaxHp());
        su.addAttribute(StatusUpdatePacket.CUR_HP, (int) getCurrentHp());
        su.addAttribute(StatusUpdatePacket.MAX_MP, getMaxMp());
        su.addAttribute(StatusUpdatePacket.CUR_MP, (int) getCurrentMp());
        su.addAttribute(StatusUpdatePacket.MAX_CP, getMaxCp());
        su.addAttribute(StatusUpdatePacket.CUR_CP, (int) getCurrentCp());
        su.build();

        sendPacket(su);

        // Send the Server->Client packet StatusUpdate with current HP and MP to all L2PcInstance that must
        broadcastPacket(su);
    }

    public int getExp() {
        return 0;
    }

    public int getSp() {
        return 0;
    }

    public int getPvpKills() {
        return 0;
    }

    public int getPkKills() {
        return 0;
    }

    public int getKarma() {
        return 0;
    }

    public long getPvpFlag() {
        return 0;
    }

    public int getCurrentLoad() {
        return 0;
    }

    public int getMaxLoad() {
        return 0;
    }

    @Override
    public void sendDamageMessage(Entity target, int damage, boolean mcrit, boolean pcrit, boolean miss) {
        // Check if hit is missed
        if (miss) {
            if (target.isPlayer()) {
                SystemMessagePacket sm = SystemMessagePacket.getSystemMessage(SystemMessageId.C1_EVADED_C2_ATTACK);
                sm.addPcName((PlayerInstance) target);
                sm.addCharName(this);
                sm.writeMe();
                ((PlayerInstance) target).sendPacket(sm);
            }
            SystemMessagePacket sm = SystemMessagePacket.getSystemMessage(SystemMessageId.C1_ATTACK_WENT_ASTRAY);
            sm.addPcName(this);
            sendPacket(sm);
            sm.writeMe();
            return;
        }

        // Check if hit is critical
        if (pcrit) {
            SystemMessagePacket sm = SystemMessagePacket.getSystemMessage(SystemMessageId.C1_HAD_CRITICAL_HIT);
            sm.addPcName(this);
            sm.writeMe();
            sendPacket(sm);
        }

        if (mcrit) {
            sendPacket(SystemMessageId.CRITICAL_HIT_MAGIC);
        }

        final SystemMessagePacket sm;

        if ((target.isInvul() || target.isHpBlocked()) && !target.isNpc()) {
            sm = SystemMessagePacket.getSystemMessage(SystemMessageId.ATTACK_WAS_BLOCKED);
        } else {
            sm = SystemMessagePacket.getSystemMessage(SystemMessageId.C1_DONE_S3_DAMAGE_TO_C2);
            sm.addPcName(this);
            sm.addCharName(target);
            sm.addInt(damage);
        }

        sm.writeMe();
        sendPacket(sm);
    }
}
