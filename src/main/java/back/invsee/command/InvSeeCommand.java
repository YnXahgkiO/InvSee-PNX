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
import cn.nukkit.entity.Entity;

import java.util.List;
import java.util.Map;

public final class InvSeeCommand extends Command {

    private final InvSeePlayerList playerList;

    public InvSeeCommand(InvSeePlayerList playerList) {
        super("invsee", "View a player's inventory", "/invsee <player>");
        this.playerList = playerList;
        this.setPermission("invsee.inventory.view;invsee.inventory.view.self");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("player", CommandParamType.TARGET)
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

        List<Entity> targets = result.getValue().getResult(0);
        if (targets.isEmpty()) {
            log.addNoTargetMatch().output();
            return 0;
        }

        Entity target = targets.get(0);
        String targetName = target instanceof Player p ? p.getName() : target.getName();
        if (viewer.getName().equalsIgnoreCase(targetName)) {
            log.addError("You cannot view your own inventory.").output();
            return 0;
        }

        if (!viewer.hasPermission("invsee.inventory.view")) {
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

        session.inventoryMenu.send(viewer);
        return 1;
    }
}
