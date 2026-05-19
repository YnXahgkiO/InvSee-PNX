package back.invsee.player;

import back.invsee.InvSeeEventListener;
import back.invsee.InvSeePlugin;
import cn.nukkit.Player;
import cn.nukkit.Server;

import java.util.HashMap;
import java.util.Map;

public final class InvSeePlayerList {

    private final Map<String, InvSeePlayer> players = new HashMap<>();
    private final Map<String, Player> onlinePlayers = new HashMap<>();
    private InvSeePlugin plugin;

    public void init(InvSeePlugin plugin) {
        this.plugin = plugin;
        Server.getInstance().getPluginManager().registerEvents(new InvSeeEventListener(this), plugin);

        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            onlinePlayers.put(player.getName().toLowerCase(), player);
        }
    }

    public InvSeePlayer getOrCreate(String playerName) throws IllegalArgumentException {
        String key = playerName.toLowerCase();
        InvSeePlayer existing = players.get(key);
        if (existing != null) return existing;

        Player online = Server.getInstance().getPlayer(playerName);
        if (online == null && !Server.getInstance().hasOfflinePlayerData(playerName)) {
            throw new IllegalArgumentException("Player \"" + playerName + "\" not found.");
        }

        String resolvedName = online != null ? online.getName() : playerName;
        InvSeePlayer session = new InvSeePlayer(resolvedName, this);
        session.init();
        players.put(resolvedName.toLowerCase(), session);
        return session;
    }

    public InvSeePlayer getWatched(Player player) {
        return players.get(player.getName().toLowerCase());
    }

    public Player getOnlinePlayer(String playerName) {
        return onlinePlayers.get(playerName.toLowerCase());
    }

    public void onPlayerJoin(Player player) {
        String key = player.getName().toLowerCase();
        onlinePlayers.put(key, player);
        InvSeePlayer session = players.get(key);
        if (session != null) {
            session.onPlayerOnline(player);
        }
    }

    public void onPlayerQuit(Player player) {
        String key = player.getName().toLowerCase();
        onlinePlayers.remove(key);
        InvSeePlayer session = players.get(key);
        if (session != null) {
            session.onPlayerOffline();
        }
    }

    public void tryGarbageCollect(String playerName) {
        String key = playerName.toLowerCase();
        InvSeePlayer session = players.get(key);
        if (session == null) return;
        if (session.inventoryMenu.getInventory().getViewers().isEmpty()
                && session.enderInventoryMenu.getInventory().getViewers().isEmpty()) {
            players.remove(key);
            session.destroy();
        }
    }

    public void close() {
        for (InvSeePlayer session : players.values()) {
            session.destroy();
        }
        players.clear();
    }
}
