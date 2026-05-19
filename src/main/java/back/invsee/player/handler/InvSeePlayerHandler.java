package back.invsee.player.handler;

import back.invsee.player.InvSeePlayer;
import cn.nukkit.item.Item;

public interface InvSeePlayerHandler {

    void init(InvSeePlayer session);

    void destroy(InvSeePlayer session);

    void syncMenuSlotToPlayer(InvSeePlayer session, int menuSlot, Item item);

    void syncEnderMenuSlotToPlayer(InvSeePlayer session, int slot, Item item);
}
