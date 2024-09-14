package com.shnok.javaserver.service.factory;

import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.util.PrimeFinder;
import lombok.extern.log4j.Log4j2;

import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
public class BitSetIDFactory extends IdFactoryService
{
    public static final int FIRST_OID = 0x00000000;
    public static final int LAST_OID = 0x7FFFFFFF;
    public static final int FREE_OBJECT_ID_SIZE = LAST_OID - FIRST_OID;

    private BitSet freeIds;
    private AtomicInteger freeIdCount;
    private AtomicInteger nextFreeId;

    public class BitSetCapacityCheck implements Runnable {
        @Override
        public void run()
        {
            if (reachingBitSetCapacity())
            {
                increaseBitSetCapacity();
            }
        }

    }

    protected BitSetIDFactory() {
        super();
        ThreadPoolManagerService.getInstance().scheduleGeneralAtFixedRate(new BitSetCapacityCheck(), 30000, 30000);
        initialize();
        log.info("[IDFactory] {} ID's available.", freeIds.size());
    }

    public synchronized void initialize() {
        try {
            freeIds = new BitSet(PrimeFinder.nextPrime(100000));
            freeIds.clear();
            freeIdCount = new AtomicInteger(FREE_OBJECT_ID_SIZE);

            for (int usedObjectId : extractUsedObjectIDs()) {
                int objectID = usedObjectId - FIRST_OID;
                if (objectID < 0) {
                    log.warn("Object ID {} in DB is less than minimum ID of " + FIRST_OID, usedObjectId);
                    continue;
                }
                freeIds.set(usedObjectId - FIRST_OID);
                freeIdCount.decrementAndGet();
            }

            nextFreeId = new AtomicInteger(freeIds.nextClearBit(0));
            initialized = true;
        } catch (Exception e) {
            initialized = false;
            log.error("BitSet ID Factory could not be initialized correctly", e);
        }
    }

    @Override
    public synchronized void releaseId(int objectID) {
        if ((objectID - FIRST_OID) > -1) {
            freeIds.clear(objectID - FIRST_OID);
            freeIdCount.incrementAndGet();
        } else {
            log.warn("BitSet ID Factory: release objectID {} failed (< " + FIRST_OID + ")", objectID);
        }
    }

    @Override
    public synchronized int getNextId() {
        int newID = nextFreeId.get();
        freeIds.set(newID);
        freeIdCount.decrementAndGet();

        int nextFree = freeIds.nextClearBit(newID);

        if (nextFree < 0) {
            nextFree = freeIds.nextClearBit(0);
        }
        if (nextFree < 0) {
            if (freeIds.size() < FREE_OBJECT_ID_SIZE) {
                increaseBitSetCapacity();
            } else {
                throw new NullPointerException("Ran out of valid Id's.");
            }
        }

        log.debug("[IDFACTORY] Next ID: {}.", newID);

        nextFreeId.set(nextFree);

        return newID + FIRST_OID;
    }

    @Override
    public synchronized int size() {
        return freeIdCount.get();
    }

    protected synchronized int usedIdCount() {
        return (size() - FIRST_OID);
    }

    protected synchronized boolean reachingBitSetCapacity() {
        return PrimeFinder.nextPrime((usedIdCount() * 11) / 10) > freeIds.size();
    }

    protected synchronized void increaseBitSetCapacity() {
        BitSet newBitSet = new BitSet(PrimeFinder.nextPrime((usedIdCount() * 11) / 10));
        newBitSet.or(freeIds);
        freeIds = newBitSet;
    }
}
