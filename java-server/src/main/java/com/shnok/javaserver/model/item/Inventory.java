package com.shnok.javaserver.model.item;

import com.shnok.javaserver.db.entity.DBEtcItem;
import com.shnok.javaserver.db.entity.DBItem;
import com.shnok.javaserver.enums.ItemLocation;
import com.shnok.javaserver.enums.item.EtcItemType;
import com.shnok.javaserver.enums.item.ItemSlot;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public abstract class Inventory extends ItemContainer {
    private final ItemInstance[] equipped;
    protected int totalWeight;

    protected Inventory() {
        super();
        equipped = new ItemInstance[15];
    }

    public void setEquippedItems(List<DBItem> itemData) {}

    @Override
    protected Entity getOwner() {
        return null;
    }

    protected abstract ItemLocation getEquipLocation();

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
                if (!(item.getItem() instanceof DBEtcItem) || (item.getItem().getType() != EtcItemType.arrow)) {
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
                if (equipped[ItemSlot.lrhand.getValue()] != null) {
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
                if (equipped[ItemSlot.lear.getValue()] == null) {
                    setEquipItem(ItemSlot.lear, item);
                } else if (equipped[ItemSlot.rear.getValue()] == null) {
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
                if (equipped[ItemSlot.lfinger.getValue()] == null) {
                    setEquipItem(ItemSlot.lfinger, item);
                } else if (equipped[ItemSlot.rfinger.getValue()] == null) {
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
        return equipped[slot.getValue()];
    }

    public ItemInstance setEquipItem(ItemSlot slot, ItemInstance item) {
        ItemInstance old = equipped[slot.getValue()];
        if (old != item) {
            if (old != null) {
                equipped[slot.getValue()] = null;
                // Put old item from equipment slot to base location
                old.setLocation(getBaseLocation());
                //TODO: Unequip old item
                //TODO: notify player
                //TODO: update db
            }
            // Add new item in slot of equipment
            if (item != null) {
                equipped[slot.getValue()] = item;
                item.setLocation(getEquipLocation(), slot.getValue());
                //TODO: notify player
                //TODO: update db
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
        for (byte i = 0; i < equipped.length; i++) {
            if (equipped[i] == item) {
                unEquipItemInSlot(ItemSlot.getSlot(i));
            }
        }

        super.removeItem(item);
    }

    public synchronized ItemInstance unEquipItemInSlot(ItemSlot slot) {
        return setEquipItem(slot, null);
    }
}
