package back.invsee.player.handler;

import back.invsee.player.InvSeePlayer;
import back.invsee.utils.InvCombiner;
import back.invsee.utils.OfflinePlayerInventory;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public final class OfflineInvSeePlayerHandler implements InvSeePlayerHandler {

    @Override
    public void init(InvSeePlayer session) {
    }

    @Override
    public void destroy(InvSeePlayer session) {
        CompoundTag nbt = Server.getInstance().getOfflinePlayerData(session.playerName);
        if (nbt == null) return;

        OfflinePlayerInventory offline = new OfflinePlayerInventory(nbt);

        Map<Integer, Item> menuContents = session.inventoryMenu.getInventory().getContents();
        Map<Integer, Item> mainOut = new HashMap<>();
        Map<Integer, Item> armorOut = new HashMap<>();
        Item[] offhandOut = {Item.AIR};

        InvCombiner.splitMenuContents(menuContents, mainOut, armorOut, offhandOut);

        offline.writeInventory(mainOut, armorOut);
        offline.writeOffhandItem(offhandOut[0]);
        offline.writeEnderInventory(session.enderInventoryMenu.getInventory().getContents());

        Server.getInstance().saveOfflinePlayerData(session.playerName, offline.getData());
    }

    @Override
    public void syncMenuSlotToPlayer(InvSeePlayer session, int menuSlot, Item item) {
    }

    @Override
    public void syncEnderMenuSlotToPlayer(InvSeePlayer session, int slot, Item item) {
    }
}
