package back.invsee.utils;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.TextFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class InvCombiner {

    public static final Map<Integer, Integer> ARMOR_TO_MENU_SLOTS = Map.of(
            0, 47,
            1, 48,
            2, 50,
            3, 51
    );

    public static final Map<Integer, Integer> MENU_TO_ARMOR_INDEX = Map.of(
            47, 0,
            48, 1,
            50, 2,
            51, 3
    );

    public static final int OFFHAND_MENU_SLOT = 53;

    public static final Set<Integer> FILLER_SLOTS = Set.of(45, 46, 49, 52);

    public static Map<Integer, Item> combine(
            Map<Integer, Item> mainInventory,
            Map<Integer, Item> armorContents,
            Item offhandItem
    ) {
        Map<Integer, Item> result = new HashMap<>(mainInventory);

        for (var entry : ARMOR_TO_MENU_SLOTS.entrySet()) {
            Item item = armorContents.get(entry.getKey());
            if (item != null && !item.isNull()) {
                result.put(entry.getValue(), item);
            }
        }

        if (offhandItem != null && !offhandItem.isNull()) {
            result.put(OFFHAND_MENU_SLOT, offhandItem);
        }

        addDecorations(result);
        return result;
    }

    public static void splitMenuContents(
            Map<Integer, Item> menuContents,
            Map<Integer, Item> mainOut,
            Map<Integer, Item> armorOut,
            Item[] offhandOut
    ) {
        for (var entry : menuContents.entrySet()) {
            int slot = entry.getKey();
            Item item = entry.getValue();
            if (FILLER_SLOTS.contains(slot)) continue;
            if (slot < 36) {
                mainOut.put(slot, item);
            } else if (MENU_TO_ARMOR_INDEX.containsKey(slot)) {
                armorOut.put(MENU_TO_ARMOR_INDEX.get(slot), item);
            } else if (slot == OFFHAND_MENU_SLOT) {
                offhandOut[0] = item;
            }
        }
    }

    private static void addDecorations(Map<Integer, Item> result) {
        result.put(45, filler(" "));
        result.put(46, filler(TextFormat.RESET + "" + TextFormat.GRAY + "Helmet \u2192"));
        result.put(49, filler(TextFormat.RESET + "" + TextFormat.GRAY + "\u2190 Chestplate \u2502 Leggings \u2192"));
        result.put(52, filler(TextFormat.RESET + "" + TextFormat.GRAY + "\u2190 Boots \u2502 Offhand \u2192"));
    }

    private static Item filler(String name) {
        return Item.get(ItemID.STAINED_GLASS_PANE, 7).setCustomName(name);
    }
}
