package com.shnok.javaserver.model.knownlist;

import com.shnok.javaserver.dto.serverpackets.NpcInfoPacket;
import com.shnok.javaserver.dto.serverpackets.RemoveObjectPacket;
import com.shnok.javaserver.dto.serverpackets.UserInfoPacket;
import com.shnok.javaserver.model.GameObject;
import com.shnok.javaserver.model.entity.Entity;
import com.shnok.javaserver.model.entity.ItemInstance;
import com.shnok.javaserver.model.entity.NpcInstance;
import com.shnok.javaserver.model.entity.PlayerInstance;

public class PlayerKnownList extends EntityKnownList
{
    public PlayerKnownList(PlayerInstance activeChar) {
        super(activeChar);
    }

    /**
     * Add a visible GameObject to PlayerInstance knownObjects and knownPlayer (if necessary) and send Server-Client Packets needed to inform the PlayerInstance of its state and actions in progress.
     *  object is a ItemInstance  :
     * Send Server-Client Packet DropItem/SpawnItem to the PlayerInstance
     *  object is a NpcInstance  :
     * Send Server-Client Packet NpcInfo to the PlayerInstance Send Server->Client packet MoveToPawn/CharMoveToLocation and AutoAttackStart to the PlayerInstance
     *  object is a PlayerInstance  :
     * Send Server-Client Packet CharInfo to the PlayerInstance If the object has a private store, Send Server-Client Packet PrivateStoreMsgSell to the PlayerInstance Send Server->Client packet MoveToPawn/CharMoveToLocation and AutoAttackStart to the PlayerInstance
     * 
     * @param object The GameObject to add to knownObjects and knownPlayer
     */
    @Override
    public boolean addKnownObject(GameObject object) {
        return addKnownObject(object, null);
    }

    @Override
    public boolean addKnownObject(GameObject object, Entity dropper) {
        if (!super.addKnownObject(object, dropper)) {
            return false;
        }

        if (object instanceof ItemInstance){
            if (dropper != null) {
              //  getActiveChar().sendPacket(new DropItem((L2ItemInstance) object, dropper.getObjectId()));
            } else {
             //   getActiveChar().sendPacket(new SpawnItem((L2ItemInstance) object));
            }
        } else if (object instanceof NpcInstance) {
            getActiveChar().sendPacket(new NpcInfoPacket((NpcInstance) object));
        } else if (object instanceof PlayerInstance) {
            PlayerInstance otherPlayer = (PlayerInstance) object;
            getActiveChar().sendPacket(new UserInfoPacket(otherPlayer));
        }

        /*if (object instanceof Entity)
        {
            // Update the state of the Entity object client side by sending Server->Client packet MoveToPawn/CharMoveToLocation and AutoAttackStart to the PlayerInstance
            Entity obj = (Entity) object;
            obj.getAI().describeStateToPlayer(getActiveChar());
        }*/

        return true;
    }

    /**
     * Remove a GameObject from PlayerInstance knownObjects and knownPlayer (if necessary) and send Server-Client Packet DeleteObject to the PlayerInstance.
     * 
     * @param object The GameObject to remove from knownObjects and knownPlayer
     */
    @Override
    public boolean removeKnownObject(GameObject object)
    {
        if (!super.removeKnownObject(object)) {
            return false;
        }

        // Send Server-Client Packet DeleteObject to the PlayerInstance
        getActiveChar().sendPacket(new RemoveObjectPacket(object.getId()));

        return true;
    }

    @Override
    public final PlayerInstance getActiveChar() {
        return (PlayerInstance) super.getActiveChar();
    }

    @Override
    public int getDistanceToForgetObject(GameObject object) {
        int knownListSize = getKnownObjects().size();
        if (knownListSize <= 25) {
            return 80;
        }
        if (knownListSize <= 35) {
            return 68;
        }
        if (knownListSize <= 70) {
            return 55;
        }
        return 44;
    }

    @Override
    public int getDistanceToWatchObject(GameObject object)
    {
        int knownListSize = getKnownObjects().size();
        if (knownListSize <= 25) {
            return 65;
        }
        if (knownListSize <= 35) {
            return 55;
        }
        if (knownListSize <= 70) {
            return 44;
        }
        return 32;
    }
}