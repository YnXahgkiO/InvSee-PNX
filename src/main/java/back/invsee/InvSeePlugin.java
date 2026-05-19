package back.invsee;

import back.invsee.command.EnderInvSeeCommand;
import back.invsee.command.InvSeeCommand;
import back.invsee.player.InvSeePlayerList;
import cn.nukkit.plugin.PluginBase;

public final class InvSeePlugin extends PluginBase {

    private static InvSeePlugin instance;
    private InvSeePlayerList playerList;

    @Override
    public void onEnable() {
        instance = this;
        playerList = new InvSeePlayerList();
        playerList.init(this);
        getServer().getCommandMap().register("invsee", new InvSeeCommand(playerList));
        getServer().getCommandMap().register("invsee", new EnderInvSeeCommand(playerList));
        getLogger().info("InvSeePNX enabled.");
    }

    @Override
    public void onDisable() {
        if (playerList != null) {
            playerList.close();
        }
    }

    public static InvSeePlugin getInstance() {
        return instance;
    }

    public InvSeePlayerList getPlayerList() {
        return playerList;
    }
}
