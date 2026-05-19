package back.invsee;

import back.invsee.player.InvSeePlayer;
import back.invsee.player.InvSeePlayerList;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityArmorChangeEvent;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.network.protocol.types.itemstack.ContainerSlotType;

public final class InvSeeEventListener implements Listener {

    private final InvSeePlayerList playerList;

    public InvSeeEventListener(InvSeePlayerList playerList) {
        this.playerList = playerList;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        playerList.onPlayerJoin(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        playerList.onPlayerQuit(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryChange(EntityInventoryChangeEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;

        InvSeePlayer session = playerList.getWatched(player);
        if (session == null || session.isSyncing()) return;

        ContainerSlotType slotType = event.getSlotType();
        int slot = event.getSlot();

        if (slotType == ContainerSlotType.OFFHAND) {
            session.onOffhandChange(event.getNewItem());
        } else if (slot < 36) {
            session.onPlayerMainInvChange(slot, event.getNewItem());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onArmorChange(EntityArmorChangeEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;

        InvSeePlayer session = playerList.getWatched(player);
        if (session == null || session.isSyncing()) return;

        int slot = event.getSlot();
        if (slot >= HumanInventory.ARMORS_INDEX && slot < HumanInventory.ARMORS_INDEX + 4) {
            session.onArmorChange(slot, event.getNewItem());
        }
    }
}
