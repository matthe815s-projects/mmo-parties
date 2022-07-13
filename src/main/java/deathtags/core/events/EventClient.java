package deathtags.core.events;

import deathtags.core.MMOParties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
@OnlyIn(Dist.CLIENT)
public class EventClient {
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void disconnectFromServer(ClientPlayerNetworkEvent.LoggedOutEvent event)
    {
        MMOParties.localParty = null;
    }

}
