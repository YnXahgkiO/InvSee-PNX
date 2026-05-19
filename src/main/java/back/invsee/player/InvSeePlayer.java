package back.invsee.player;

import back.invsee.player.handler.InvSeePlayerHandler;
import back.invsee.player.handler.OfflineInvSeePlayerHandler;
import back.invsee.player.handler.OnlineInvSeePlayerHandler;
import back.invsee.utils.InvCombiner;
import back.invsee.utils.OfflinePlayerInventory;
import back.invmenupnx.InvMenu;
import back.invmenupnx.transaction.InvMenuTransaction;
import back.invmenupnx.transaction.InvMenuTransactionResult;
import back.invmenupnx.type.InvMenuType;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.HumanInventory;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public final class InvSeePlayer {

    public final String playerName;
    public final InvMenu inventoryMenu;
    public final InvMenu enderInventoryMenu;

    private final InvSeePlayerList playerList;
    private InvSeePlayerHandler handler;
    private boolean syncing = false;

    InvSeePlayer(String playerName, InvSeePlayerList playerList) {
        this.playerName = playerName;
        this.playerList = playerList;
        this.inventoryMenu = InvMenu.create(InvMenuType.DOUBLE_CHEST);
        this.enderInventoryMenu = InvMenu.create(InvMenuType.CHEST);
    }

    void init() {
        Player onlinePlayer = playerList.getOnlinePlayer(playerName);

        Map<Integer, Item> mainInv;
        Map<Integer, Item> armorInv;
        Item offhandItem;
        Map<Integer, Item> enderInv;

        if (onlinePlayer != null) {
            Map<Integer, Item> allContents = onlinePlayer.getInventory().getContents();
            mainInv = new HashMap<>();
            armorInv = new HashMap<>();
            for (var entry : allContents.entrySet()) {
                if (entry.getKey() < 36) {
                    mainInv.put(entry.getKey(), entry.getValue());
                } else if (entry.getKey() >= HumanInventory.ARMORS_INDEX && entry.getKey() < HumanInventory.ARMORS_INDEX + 4) {
                    armorInv.put(entry.getKey() - HumanInventory.ARMORS_INDEX, entry.getValue());
                }
            }
            offhandItem = onlinePlayer.getOffhandInventory().getItem(0);
            enderInv = onlinePlayer.getEnderChestInventory().getContents();
        } else {
            CompoundTag nbt = Server.getInstance().getOfflinePlayerData(playerName);
            if (nbt == null) throw new IllegalArgumentException("Player \"" + playerName + "\" not found.");
            OfflinePlayerInventory offline = new OfflinePlayerInventory(nbt);
            mainInv = offline.readInventory();
            armorInv = offline.readArmorInventory();
            offhandItem = offline.readOffhandItem();
            enderInv = offline.readEnderInventory();
        }

        inventoryMenu.getInventory().setContents(InvCombiner.combine(mainInv, armorInv, offhandItem));
        enderInventoryMenu.getInventory().setContents(enderInv);

        inventoryMenu.setTitle(playerName + "'s Inventory");
        inventoryMenu.setListener(this::onInventoryMenuTransaction);
        inventoryMenu.setCloseListener((viewer, inv) -> playerList.tryGarbageCollect(playerName));

        enderInventoryMenu.setTitle(playerName + "'s Ender Chest");
        enderInventoryMenu.setListener(this::onEnderMenuTransaction);
        enderInventoryMenu.setCloseListener((viewer, inv) -> playerList.tryGarbageCollect(playerName));

        if (onlinePlayer != null) {
            setHandler(new OnlineInvSeePlayerHandler(onlinePlayer));
        } else {
            setHandler(new OfflineInvSeePlayerHandler());
        }
    }

    void onPlayerOnline(Player player) {
        setHandler(new OnlineInvSeePlayerHandler(player));
    }

    void onPlayerOffline() {
        setHandler(new OfflineInvSeePlayerHandler());
    }

    void destroy() {
        if (handler != null) {
            handler.destroy(this);
            handler = null;
        }
        inventoryMenu.closeAll();
        enderInventoryMenu.closeAll();
        inventoryMenu.setListener(null);
        inventoryMenu.setCloseListener(null);
        enderInventoryMenu.setListener(null);
        enderInventoryMenu.setCloseListener(null);
    }

    public void onPlayerMainInvChange(int flatSlot, Item item) {
        syncing = true;
        try {
            inventoryMenu.getInventory().setItem(flatSlot, item);
        } finally {
            syncing = false;
        }
    }

    public void onArmorChange(int humanInvSlot, Item item) {
        syncing = true;
        try {
            int armorIndex = humanInvSlot - HumanInventory.ARMORS_INDEX;
            Integer menuSlot = InvCombiner.ARMOR_TO_MENU_SLOTS.get(armorIndex);
            if (menuSlot != null) {
                inventoryMenu.getInventory().setItem(menuSlot, item);
            }
        } finally {
            syncing = false;
        }
    }

    public void onOffhandChange(Item item) {
        syncing = true;
        try {
            inventoryMenu.getInventory().setItem(InvCombiner.OFFHAND_MENU_SLOT, item);
        } finally {
            syncing = false;
        }
    }

    public boolean isSyncing() {
        return syncing;
    }

    public void setSyncing(boolean syncing) {
        this.syncing = syncing;
    }

    private void setHandler(InvSeePlayerHandler newHandler) {
        if (handler != null) {
            handler.destroy(this);
        }
        handler = newHandler;
        handler.init(this);
    }

    private InvMenuTransactionResult onInventoryMenuTransaction(InvMenuTransaction txn) {
        int slot = txn.getSlot();
        if (InvCombiner.FILLER_SLOTS.contains(slot)) return txn.discard();

        Player viewer = txn.getPlayer();
        boolean isModifiableSlot = slot < 36
                || InvCombiner.MENU_TO_ARMOR_INDEX.containsKey(slot)
                || slot == InvCombiner.OFFHAND_MENU_SLOT;

        if (!isModifiableSlot) return txn.discard();
        if (!viewer.hasPermission("invsee.inventory.modify")) return txn.discard();

        if (handler != null) {
            handler.syncMenuSlotToPlayer(this, slot, txn.getItemClickedWith());
        }
        return txn.proceed();
    }

    private InvMenuTransactionResult onEnderMenuTransaction(InvMenuTransaction txn) {
        Player viewer = txn.getPlayer();
        if (!viewer.hasPermission("invsee.enderinventory.modify")) return txn.discard();

        if (handler != null) {
            handler.syncEnderMenuSlotToPlayer(this, txn.getSlot(), txn.getItemClickedWith());
        }
        return txn.proceed();
    }
}
