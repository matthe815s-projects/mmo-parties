package deathtags.core.events;

import deathtags.core.MMOParties;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber
public class EventClient {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void disconnectFromServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        MMOParties.localParty = null;
    }

}