package dev.matthe815.mmoparties.forge.events;

import dev.matthe815.mmoparties.common.events.EventCommon;
import dev.matthe815.mmoparties.common.stats.Party;
import dev.matthe815.mmoparties.common.stats.PlayerStats;
import dev.matthe815.mmoparties.forge.api.PartyHelper;
import dev.matthe815.mmoparties.forge.api.relation.EnumRelation;
import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.forge.core.MMOParties;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
        EventCommon.onPlayerJoined(event.getEntity());
    }

    /**
     * Handle a player leaving a server/world.
     * @param event
     */
    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        EventCommon.onPlayerLeave(event.getEntity());
    }

    /**
     * Handle canceling PVP damage when player attacking player.
     * @param event
     */
    @SubscribeEvent(priority= EventPriority.HIGHEST)
    public void OnPlayerHurt(LivingHurtEvent event)
    {
        event.setCanceled(EventCommon.OnPlayerHurt(event.getEntity(), event.getSource().getDirectEntity()));
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
