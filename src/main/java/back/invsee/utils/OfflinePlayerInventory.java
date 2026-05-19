package back.invsee.utils;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.HashMap;
import java.util.Map;

public final class OfflinePlayerInventory {

    private final CompoundTag data;

    public OfflinePlayerInventory(CompoundTag data) {
        this.data = data;
    }

    public Map<Integer, Item> readInventory() {
        Map<Integer, Item> result = new HashMap<>();
        ListTag<CompoundTag> list = data.getList("Inventory", CompoundTag.class);
        if (list == null) return result;
        for (CompoundTag tag : list.getAll()) {
            int slot = tag.getByte("Slot") & 0xFF;
            if (slot < 36) {
                result.put(slot, NBTIO.getItemHelper(tag));
            }
        }
        return result;
    }

    public Map<Integer, Item> readArmorInventory() {
        Map<Integer, Item> result = new HashMap<>();
        ListTag<CompoundTag> list = data.getList("Inventory", CompoundTag.class);
        if (list == null) return result;
        for (CompoundTag tag : list.getAll()) {
            int slot = tag.getByte("Slot") & 0xFF;
            if (slot >= 36 && slot < 40) {
                result.put(slot - 36, NBTIO.getItemHelper(tag));
            }
        }
        return result;
    }

    public Item readOffhandItem() {
        CompoundTag offhand = data.getCompound("OffInventory");
        if (offhand == null) return Item.AIR;
        return NBTIO.getItemHelper(offhand);
    }

    public Map<Integer, Item> readEnderInventory() {
        Map<Integer, Item> result = new HashMap<>();
        ListTag<CompoundTag> list = data.getList("EnderItems", CompoundTag.class);
        if (list == null) return result;
        for (CompoundTag tag : list.getAll()) {
            int slot = tag.getByte("Slot") & 0xFF;
            result.put(slot, NBTIO.getItemHelper(tag));
        }
        return result;
    }

    public void writeInventory(Map<Integer, Item> mainInventory, Map<Integer, Item> armorInventory) {
        ListTag<CompoundTag> list = new ListTag<>();
        for (var entry : mainInventory.entrySet()) {
            if (!entry.getValue().isNull()) {
                list.add(NBTIO.putItemHelper(entry.getValue(), entry.getKey()));
            }
        }
        for (var entry : armorInventory.entrySet()) {
            if (!entry.getValue().isNull()) {
                list.add(NBTIO.putItemHelper(entry.getValue(), entry.getKey() + 36));
            }
        }
        data.putList("Inventory", list);
    }

    public void writeOffhandItem(Item item) {
        data.putCompound("OffInventory", NBTIO.putItemHelper(item, 0));
    }

    public void writeEnderInventory(Map<Integer, Item> enderInventory) {
        ListTag<CompoundTag> list = new ListTag<>();
        for (var entry : enderInventory.entrySet()) {
            if (!entry.getValue().isNull()) {
                list.add(NBTIO.putItemHelper(entry.getValue(), entry.getKey()));
            }
        }
        data.putList("EnderItems", list);
    }

    public CompoundTag getData() {
        return data;
    }
}
