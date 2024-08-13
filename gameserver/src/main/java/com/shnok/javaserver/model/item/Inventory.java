package com.shnok.javaserver.model.item;

import com.shnok.javaserver.db.entity.DBArmor;
import com.shnok.javaserver.db.entity.DBEtcItem;
import com.shnok.javaserver.db.entity.DBItem;
import com.shnok.javaserver.db.entity.DBWeapon;
import com.shnok.javaserver.enums.ItemLocation;
import com.shnok.javaserver.enums.item.EtcItemType;
import com.shnok.javaserver.enums.item.ItemSlot;
import com.shnok.javaserver.model.item.listeners.GearListener;
import com.shnok.javaserver.model.item.listeners.StatsListener;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import javolution.util.FastList;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public abstract class Inventory extends ItemContainer {
    private final ItemInstance[] gear;
    private List<GearListener> gearListeners;
    protected int totalWeight;

    protected Inventory(Entity owner) {
        super(owner);
        gear = new ItemInstance[15];
        if(owner.isPlayer()) {
            gearListeners = new FastList<>();
            addGearListener(new StatsListener(getOwner()));
        }
    }

    /**
     * Adds new inventory's gear listener
     * @param listener
     */
    public synchronized void addGearListener(GearListener listener) {
        gearListeners.add(listener);
    }

    /**
     * Removes a gear listener
     * @param listener
     */
    public synchronized void removeGearListener(GearListener listener) {
        gearListeners.remove(listener);
    }

    public void setEquippedItems(List<DBItem> itemData) {}

    protected abstract ItemLocation getEquipLocation();

    /**
     * Equips item and returns list of alterations
     * @param item : L2ItemInstance corresponding to the item
     * @return L2ItemInstance[] : list of alterations
     */
    public ItemInstance[] equipItemAndRecord(ItemInstance item) {
        Inventory.ChangeRecorder recorder = newRecorder();

        try {
            equipItem(item);
        } finally {
            removeGearListener(recorder);
        }

        return recorder.getChangedItems();
    }

    public synchronized void equipItem(ItemInstance item) {
        ItemSlot targetSlot = ItemSlot.none;
        DBItem itemData = item.getItem();
        if(itemData instanceof DBEtcItem) {
            if(itemData.getType() == EtcItemType.arrow) {
                targetSlot = ItemSlot.rhand;
            } else {
                log.warn("Tried to equip a wrong item.");
                return;
            }
        } else {
            targetSlot = itemData.getBodyPart();
        }

        switch (targetSlot) {
            case lrhand: {
                if (setEquipItem(ItemSlot.lhand, null) != null) {
                    // exchange 2h for 2h
                    setEquipItem(ItemSlot.rhand, null);
                    setEquipItem(ItemSlot.lhand, null);
                } else {
                    setEquipItem(ItemSlot.rhand, null);
                }

                setEquipItem(ItemSlot.rhand, item);
                setEquipItem(ItemSlot.lrhand, item);
                break;
            }
            case lhand: {
                if (!(item.getItem() instanceof DBEtcItem) ||
                        (item.getItem().getType() != EtcItemType.arrow)) {
                    ItemInstance old1 = setEquipItem(ItemSlot.lrhand, null);

                    if (old1 != null) {
                        setEquipItem(ItemSlot.rhand, null);
                    }
                }

                setEquipItem(ItemSlot.lhand, null);
                setEquipItem(ItemSlot.lhand, item);
                break;
            }
            case rhand: {
                if (gear[ItemSlot.lrhand.getValue()] != null) {
                    setEquipItem(ItemSlot.lrhand, null);
                    setEquipItem(ItemSlot.lhand, null);
                    setEquipItem(ItemSlot.rhand, null);
                } else {
                    setEquipItem(ItemSlot.rhand, null);
                }

                setEquipItem(ItemSlot.rhand, item);
                break;
            }
            case lear:
            case rear:
            case earring: {
                if (gear[ItemSlot.lear.getValue()] == null) {
                    setEquipItem(ItemSlot.lear, item);
                } else if (gear[ItemSlot.rear.getValue()] == null) {
                    setEquipItem(ItemSlot.rear, item);
                } else {
                    setEquipItem(ItemSlot.lear, null);
                    setEquipItem(ItemSlot.lear, item);
                }

                break;
            }
            case lfinger:
            case rfinger:
            case ring: {
                if (gear[ItemSlot.lfinger.getValue()] == null) {
                    setEquipItem(ItemSlot.lfinger, item);
                } else if (gear[ItemSlot.rfinger.getValue()] == null) {
                    setEquipItem(ItemSlot.rfinger, item);
                } else {
                    setEquipItem(ItemSlot.lfinger, null);
                    setEquipItem(ItemSlot.lfinger, item);
                }

                break;
            }
            case neck:
                setEquipItem(ItemSlot.lfinger, item);
                break;
            case fullarmor:
                setEquipItem(ItemSlot.chest, null);
                setEquipItem(ItemSlot.legs, null);
                setEquipItem(ItemSlot.chest, item);
                break;
            case chest:
                setEquipItem(ItemSlot.chest, item);
                break;
            case legs: {
                // handle full armor
                ItemInstance chest = getEquippedItem(ItemSlot.chest);
                if ((chest != null) && (chest.getItem().getBodyPart() == ItemSlot.fullarmor)) {
                    setEquipItem(ItemSlot.chest, null);
                }

                setEquipItem(ItemSlot.legs, null);
                setEquipItem(ItemSlot.legs, item);
                break;
            }
            case feet:
                setEquipItem(ItemSlot.feet, item);
                break;
            case gloves:
                setEquipItem(ItemSlot.gloves, item);
                break;
            case head:
                setEquipItem(ItemSlot.head, item);
                break;
            case underwear:
                setEquipItem(ItemSlot.underwear, item);
                break;
            default:
                log.warn("unknown body slot:" + targetSlot);
        }
    }

    public ItemInstance getEquippedItem(ItemSlot slot) {
        return gear[slot.getValue()];
    }

    public int getEquippedItemId(ItemSlot slot) {
        if(gear[slot.getValue()] != null) {
            return gear[slot.getValue()].getItemId();
        }

        return 0;
    }


    public ItemInstance setEquipItem(ItemSlot slot, ItemInstance item) {
        ItemInstance old = gear[slot.getValue()];
        if (old != item) {
            if (old != null) {
                gear[slot.getValue()] = null;
                // Put old item from equipment slot to base location
                old.setLocation(getBaseLocation());
                old.setLastChange(ItemInstance.MODIFIED);
                //TODO: update db
                if(owner.isPlayer()) {
                    for (GearListener listener : gearListeners) {
                        if (listener == null) {
                            continue;
                        }
                        listener.notifyUnequipped(slot.getValue(), old);
                    }
                }
            }
            // Add new item in slot of equipment
            if (item != null) {
                gear[slot.getValue()] = item;
                item.setLocation(getEquipLocation(), slot.getValue());
                item.setLastChange(ItemInstance.MODIFIED);
                log.debug("[ITEM][{}] Equipped {} in slot {}", getOwner().getId(), item.getItemId(), slot);
                //TODO: update db
                if(owner.isPlayer()) {
                    for (GearListener listener : gearListeners) {
                        if (listener == null) {
                            continue;
                        }
                        listener.notifyEquipped(slot.getValue(), item);
                    }
                }
            }
        }
        return old;
    }


    // drops item the ground not regarding the count
    public ItemInstance dropItem(ItemInstance item, PlayerInstance actor) {
        synchronized (item) {
            if (!items.contains(item)) {
                return null;
            }

            removeItem(item);
            item.setOwnerId(0, actor);
            item.setLocation(ItemLocation.VOID);

            //TODO Update database
            //item.updateDatabase();

            refreshWeight();
        }
        return item;
    }

    // drop item from inventory by using its objectID and updates database
    public ItemInstance dropItem(int objectId, int count, PlayerInstance actor) {
        ItemInstance item = getItemByObjectId(objectId);
        if (item == null) {
            return null;
        }

        // Adjust item quantity and create new instance to drop
        if (item.getCount() > count) {
            item.changeCount(-count, actor);

            //TODO Update database
            //item.updateDatabase();

            //TODO copy the item with new count in DB

            //TODO Update database
            //item.updateDatabase();

            refreshWeight();
            return item;
        }

        // Directly drop entire item
        return dropItem(item, actor);
    }

    // adds item to inventory and equip it if necessary
    @Override
    public void addItem(ItemInstance item) {
        super.addItem(item);
        if (item.isEquipped()) {
            equipItem(item);
        }
    }

    // removes item from inventory
    @Override
    protected void removeItem(ItemInstance item) {
        // unequip item if equiped
        for (byte i = 0; i < gear.length; i++) {
            if (gear[i] == item) {
                unEquipItemInSlot(ItemSlot.getSlot(i));
            }
        }

        super.removeItem(item);
    }

    public DBWeapon getEquippedWeapon() {
        ItemInstance item = getEquippedItem(ItemSlot.rhand);
        if(item != null && item.getItem() instanceof DBWeapon) {
            return (DBWeapon) item.getItem();
        }

        item = getEquippedItem(ItemSlot.lhand);
        if(item != null && item.getItem() instanceof DBWeapon) {
            return (DBWeapon) item.getItem();
        }

        return null;
    }

    public DBArmor getEquippedSecondaryWeapon() {
        ItemInstance item = getEquippedItem(ItemSlot.lhand);
        if(item != null && item.getItem() instanceof DBArmor) {
            return (DBArmor) item.getItem();
        }

        return null;
    }

    /**
     * Returns the instance of new ChangeRecorder
     * @return ChangeRecorder
     */
    public ChangeRecorder newRecorder() {
        return new ChangeRecorder(this);
    }

    /**
     * Unequips item in body slot and returns alterations.
     * @param slot : int designating the slot of the paperdoll
     * @return L2ItemInstance[] : list of changes
     */
    public synchronized ItemInstance[] unEquipItemInSlotAndRecord(ItemSlot slot) {
        Inventory.ChangeRecorder recorder = newRecorder();
        try {
            unEquipItemInSlot(slot);
            //TODO: Update grade penalty
//            if (getOwner() != null) {
//                ((PlayerInstance) getOwner()).refreshExpertisePenalty();
//            }
        } finally {
            removeGearListener(recorder);
        }
        return recorder.getChangedItems();
    }

    public synchronized ItemInstance unEquipItemInSlot(ItemSlot slot) {
        return setEquipItem(slot, null);
    }

    public boolean isSlotUsed(ItemSlot slot) {
        return getEquippedItem(slot) != null;
    }

    /**
     * Recorder of alterations in inventory
     */
    public static final class ChangeRecorder implements GearListener
    {
        private final Inventory inventory;
        private final List<ItemInstance> changed;

        /**
         * Constructor of the ChangeRecorder
         * @param inventory
         */
        ChangeRecorder(Inventory inventory) {
            this.inventory = inventory;
            changed = new FastList<>();
            inventory.addGearListener(this);
        }

        /**
         * Add alteration in inventory when item equiped
         */
        @Override
        public void notifyEquipped(int slot, ItemInstance item) {
            if (!changed.contains(item)) {
                changed.add(item);
            }
        }

        /**
         * Add alteration in inventory when item unequiped
         */
        @Override
        public void notifyUnequipped(int slot, ItemInstance item) {
            if (!changed.contains(item)) {
                changed.add(item);
            }
        }

        /**
         * Returns alterations in inventory
         * @return L2ItemInstance[] : array of alterated items
         */
        public ItemInstance[] getChangedItems() {
            return changed.toArray(new ItemInstance[changed.size()]);
        }
    }
}
