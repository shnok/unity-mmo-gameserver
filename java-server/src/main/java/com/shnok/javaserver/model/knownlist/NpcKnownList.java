package com.shnok.javaserver.model.knownlist;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.dto.serverpackets.NpcInfoPacket;
import com.shnok.javaserver.model.GameObject;
import com.shnok.javaserver.model.entity.Entity;
import com.shnok.javaserver.model.entity.NpcInstance;
import com.shnok.javaserver.model.entity.PlayerInstance;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class NpcKnownList extends EntityKnownList
{
    public NpcKnownList(NpcInstance activeChar) {
        super(activeChar);
    }

    /**
     * Add a visible GameObject to PlayerInstance knownObjects and knownPlayer (if necessary) and send Server-Client Packets needed to inform the PlayerInstance of its state and actions in progress.
     *  object is a ItemInstance  :
     * Send Server-Client Packet DropItem/SpawnItem to the PlayerInstance
     *  object is a NpcInstance  :
     * Send Server-Client Packet NpcInfo to the PlayerInstance Send Server->Client packet MoveToPawn/CharMoveToLocation and AutoAttackStart to the PlayerInstance
     *  object is a PlayerInstance  :
     * Send Server-Client Packet CharInfo to the PlayerInstance  Send Server->Client packet MoveToPawn/CharMoveToLocation and AutoAttackStart to the PlayerInstance
     */
    @Override
    public boolean addKnownObject(GameObject object) {
        return addKnownObject(object, false);
    }

    @Override
    public boolean addKnownObject(GameObject object, boolean silent) {
        if(!super.addKnownObject(object, silent)) {
            return false;
        }

        if (object instanceof PlayerInstance) {
            if(getKnownPlayers().size() == 1) {
                getActiveChar().refreshAI();
            }

            // Share current action to player instance
            if(Config.PRINT_KNOWN_LIST_LOGS) {
                log.debug("[{}] Sharing current action to user", getActiveChar().getId());
            }
            getActiveChar().shareCurrentAction((PlayerInstance) object);
        }

        return true;
    }

    // Remove a GameObject from NpcInstance knownObjects and knownPlayer (if necessary).
    @Override
    public boolean removeKnownObject(GameObject object) {
        if(!super.removeKnownObject(object)) {
            return false;
        }

        if (object instanceof PlayerInstance) {
            if (!Config.KEEP_AI_ALIVE) {
                if (getKnownPlayers().size() == 0) {
                    getActiveChar().stopAndRemoveAI();
                }
            }

            if(Config.PRINT_KNOWN_LIST_LOGS) {
                log.debug("[{}] Removing player [{}] from known list", getActiveChar().getId(), object.getId());
            }
        }

        return true;
    }

    @Override
    public final NpcInstance getActiveChar() {
        return (NpcInstance) super.getActiveChar();
    }

    @Override
    public int getDistanceToForgetObject(GameObject object) {
        return 2 * getDistanceToWatchObject(object);
    }

    @Override
    public int getDistanceToWatchObject(GameObject object) {
        if (object instanceof PlayerInstance) {
            return 60;
        }
        return 30;
    }
}
