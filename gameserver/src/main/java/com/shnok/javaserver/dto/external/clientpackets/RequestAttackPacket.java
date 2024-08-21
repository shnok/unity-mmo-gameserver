package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Getter;

@Getter
public class RequestAttackPacket extends ClientPacket {
    private final int targetId;
    private final byte attackType;

    public RequestAttackPacket(GameClientThread client, byte[] data) {
        super(client, data);
        targetId = readI();
        attackType = readB();

        handlePacket();
    }

    @Override
    public void handlePacket() {
//
//        //GameObject object = client.getCurrentPlayer().getKnownList().getKnownObjects().get(getTargetId());
//        GameObject object = client.getCurrentPlayer();
//        if(object == null) {
//            log.warn("Trying to attack a null object.");
//        }
//        if(!(object.isEntity())) {
//            log.warn("Trying to attack a non-entity object.");
//            return;
//        }
//        if(((Entity)object).getStatus().getCurrentHp() <= 0) {
//            log.warn("Trying to attack a dead entity.");
//            return;
//        }
//
//        // ! FOR DEBUG PURPOSE
//        int damage = 25;
//        ((Entity) object).reduceCurrentHp(damage, client.getCurrentPlayer());
//        boolean critical = false;
//        Random r = new Random();
//        if(r.nextInt(2) == 0) {
//            critical = true;
//        }
//
//        // ! FOR DEBUG PURPOSE
//        // Notify known list
//        ApplyDamagePacket applyDamagePacket = new ApplyDamagePacket(
//                client.getCurrentPlayer().getId(), getTargetId(), damage, ((Entity) object).getStatus().getCurrentHp(), critical);
//        // Send packet to player's known list
//        client.getCurrentPlayer().broadcastPacket(applyDamagePacket);
//        // Send packet to player
//        client.sendPacket(applyDamagePacket);
    }
}
