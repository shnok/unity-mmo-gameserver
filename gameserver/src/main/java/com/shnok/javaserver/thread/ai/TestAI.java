package com.shnok.javaserver.thread.ai;

import com.shnok.javaserver.enums.EntityMovingReason;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.NpcInstance;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.pathfinding.node.Node;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
public class TestAI extends NpcAI implements Runnable {
    private final List<Point3D> path;
    private byte pathIndex = 0;
    private boolean increment = true;

    public TestAI(Entity owner, List<Point3D> path) {
        super(owner);
        this.path = path;
    }

    /*
    =========================
    ========= EVENT =========
    =========================
     */
    @Override
    protected void onEvtThink() {
        if (thinking || owner == null) {
            return;
        }

        if(npc == null) {
            npc = (NpcInstance) owner;
        }

        thinking = true;

        /* Is NPC waiting ? */
        if (getIntention() == Intention.INTENTION_IDLE) {
            thinkIdle();
        }

        thinking = false;
    }

    @Override
    protected void onEvtArrived() {
        super.onEvtArrived();

        if (owner.moveToNextRoutePoint()) {
            return;
        }

        if (getIntention() == Intention.INTENTION_MOVE_TO) {
            setIntention(Intention.INTENTION_IDLE);
        }

        if(getIntention() == Intention.INTENTION_ATTACK) {
            setIntention(Intention.INTENTION_ATTACK, attackTarget);
        }
    }

    @Override
    protected void onEvtAttacked(Entity attacker) {

    }


    /*
    =========================
    ========= THINK =========
    =========================
     */
    void thinkIdle() {
        /* Check if npc needs to change its intention */
        if (npc.isRandomWalk()) {
            movingReason = EntityMovingReason.Walking;

            // Update npc move speed to its walking speed
            npc.getStatus().setMoveSpeed(npc.getTemplate().getBaseWalkSpd());
            patrol();
        }
    }

    /*
    =========================
    ======= INTENTION =======
    =========================
     */
    @Override
    protected void onIntentionMoveTo(Point3D destination) {
        // Check if still running
        if (owner.moveTo(destination)) {
            return;
        }

        setIntention(Intention.INTENTION_IDLE);
    }

    @Override
    protected void onIntentionIdle() {
        super.onIntentionIdle();

        // Stop moving
        getOwner().setMoving(false);
    }

    /*
    =========================
    ========= OTHER =========
    =========================
     */

    // default monster behaviour
    private void patrol() {
        if (npc.isOnGeoData()) {
            try {
                Point3D nextPathPoint = path.get(getNextPathIndex());
                Node n = Geodata.getInstance().getClosestNodeAt(nextPathPoint);

                setIntention(Intention.INTENTION_MOVE_TO, n.getCenter());
            } catch (Exception e) {
                if(server.printPathfinder()) {
                    log.debug(e);
                }

                owner.setMoving(false);
                setIntention(Intention.INTENTION_IDLE);
            }
        }
    }

    private byte getNextPathIndex() {
        if (pathIndex == path.size() - 1) {
            increment = false;
        } else if (pathIndex == 0) {
            increment = true;
        }

        if (increment) {
            return ++pathIndex;
        } else {
            return --pathIndex;
        }
    }
}
