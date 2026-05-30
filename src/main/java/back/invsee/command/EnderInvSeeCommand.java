package back.invsee.command;

import back.invsee.player.InvSeePlayer;
import back.invsee.player.InvSeePlayerList;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.tree.ParamList;
import cn.nukkit.command.utils.CommandLogger;

import java.util.Map;

public final class EnderInvSeeCommand extends Command {

    private final InvSeePlayerList playerList;

    public EnderInvSeeCommand(InvSeePlayerList playerList) {
        super("enderinvsee", "View a player's ender chest", "/enderinvsee <name>");
        this.playerList = playerList;
        this.setPermission("invsee.enderinventory.view;invsee.enderinventory.view.self");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("name", CommandParamType.STRING)
        });
        this.enableParamTree();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return false;
    }

    @Override
    public int execute(CommandSender sender, String label, Map.Entry<String, ParamList> result, CommandLogger log) {
        if (!(sender instanceof Player viewer)) {
            log.addError("commands.generic.ingame").output();
            return 0;
        }

        String targetName = result.getValue().getResult(0);
        if (targetName == null || targetName.isBlank()) {
            log.addError("commands.generic.usage").output();
            return 0;
        }

        if (viewer.getName().equalsIgnoreCase(targetName)) {
            log.addError("You cannot view your own ender chest.").output();
            return 0;
        }

        if (!viewer.hasPermission("invsee.enderinventory.view")) {
            log.addError("commands.generic.permission").output();
            return 0;
        }

        InvSeePlayer session;
        try {
            session = playerList.getOrCreate(targetName);
        } catch (IllegalArgumentException e) {
            log.addError(e.getMessage()).output();
            return 0;
        }

        session.enderInventoryMenu.send(viewer);
        return 1;
    }
}
