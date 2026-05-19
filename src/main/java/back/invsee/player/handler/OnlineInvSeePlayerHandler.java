package back.invsee.player.handler;

import back.invsee.player.InvSeePlayer;
import back.invsee.utils.InvCombiner;
import cn.nukkit.Player;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.item.Item;

import java.util.HashMap;
import java.util.Map;

public final class OnlineInvSeePlayerHandler implements InvSeePlayerHandler {

    private final Player player;

    public OnlineInvSeePlayerHandler(Player player) {
        this.player = player;
    }

    @Override
    public void init(InvSeePlayer session) {
        session.setSyncing(true);
        try {
            Map<Integer, Item> menuContents = session.inventoryMenu.getInventory().getContents();
            Map<Integer, Item> mainOut = new HashMap<>();
            Map<Integer, Item> armorOut = new HashMap<>();
            Item[] offhandOut = {Item.AIR};

            InvCombiner.splitMenuContents(menuContents, mainOut, armorOut, offhandOut);

            for (int i = 0; i < 36; i++) {
                player.getInventory().setItem(i, mainOut.getOrDefault(i, Item.AIR));
            }
            for (var entry : armorOut.entrySet()) {
                player.getInventory().setItem(HumanInventory.ARMORS_INDEX + entry.getKey(), entry.getValue());
            }
            player.getOffhandInventory().setItem(offhandOut[0]);
            player.getEnderChestInventory().setContents(session.enderInventoryMenu.getInventory().getContents());
        } finally {
            session.setSyncing(false);
        }
    }

    @Override
    public void destroy(InvSeePlayer session) {
    }

    @Override
    public void syncMenuSlotToPlayer(InvSeePlayer session, int menuSlot, Item item) {
        session.setSyncing(true);
        try {
            if (menuSlot < 36) {
                player.getInventory().setItem(menuSlot, item);
            } else if (InvCombiner.MENU_TO_ARMOR_INDEX.containsKey(menuSlot)) {
                int armorIndex = InvCombiner.MENU_TO_ARMOR_INDEX.get(menuSlot);
                player.getInventory().setItem(HumanInventory.ARMORS_INDEX + armorIndex, item);
            } else if (menuSlot == InvCombiner.OFFHAND_MENU_SLOT) {
                player.getOffhandInventory().setItem(item);
            }
        } finally {
            session.setSyncing(false);
        }
    }

    @Override
    public void syncEnderMenuSlotToPlayer(InvSeePlayer session, int slot, Item item) {
        session.setSyncing(true);
        try {
            player.getEnderChestInventory().setItem(slot, item);
        } finally {
            session.setSyncing(false);
        }
    }
}
