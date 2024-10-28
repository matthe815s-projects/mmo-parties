package dev.matthe815.mmoparties.forge.events;

import dev.matthe815.mmoparties.common.events.EventCommon;
import dev.matthe815.mmoparties.common.stats.Party;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class EventCommonForge {
    /**
     * Global player instance, gets filled when the first player joins a server with global parties enabled.
     * All players that join after will be put into this party.
     */
    public static Party globalParty = null;

    /**
     * Handle a player joining a server/world.
     * @param event
     */
    @SubscribeEvent
    public void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event)
    {
        System.out.println("Joined");
        EventCommon.onPlayerJoined(event.player);
    }

    /**
     * Handle a player leaving a server/world.
     * @param event
     */
    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        EventCommon.onPlayerLeave(event.player);
    }

    /**
     * Handle canceling PVP damage when player attacking player.
     * @param event
     */
    @SubscribeEvent(priority= EventPriority.HIGHEST)
    public void OnPlayerHurt(LivingHurtEvent event)
    {
        event.setCanceled(EventCommon.OnPlayerHurt(event.getEntity(), event.getSource().getTrueSource()));
    }

    /**
     * Process the game ticks, specifically for teleporting and updating the member's information.
     * @param event
     */
    @SubscribeEvent
    public void OnPlayerGameTick(TickEvent.PlayerTickEvent event)
    {
        EventCommon.OnPlayerGameTick(event.player);
    }
}
