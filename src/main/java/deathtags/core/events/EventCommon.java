package deathtags.core.events;

import deathtags.api.PartyHelper;
import deathtags.api.relation.EnumRelation;
import deathtags.config.ConfigHolder;
import deathtags.core.MMOParties;
import deathtags.stats.PlayerStats;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventCommon {
    @SubscribeEvent
    public void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event)
    {
        Player player = event.getPlayer();
        if (!MMOParties.PlayerStats.containsKey(player)) MMOParties.PlayerStats.put(player, new PlayerStats( player ));
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        PlayerStats playerStats = MMOParties.GetStatsByName(event.getPlayer().getName().getString());

        playerStats.Leave(); // Leave if in a party.
        MMOParties.PlayerStats.remove(event.getPlayer()); // Remove the player's temporary data.
    }

    @SubscribeEvent(priority= EventPriority.HIGHEST)
    public void OnPlayerHurt(LivingHurtEvent event)
    {
        if (!ConfigHolder.COMMON.friendlyFireDisabled.get()) return; // Friendly fire is allowed so this doesn't matter.
        if (event.getEntity().getCommandSenderWorld().isClientSide) return; // Perform on the server only.

        if (! (event.getEntityLiving() instanceof Player)
                || ! (event.getSource().getDirectEntity() instanceof Player) ) return; // Only perform on players.

        Player player = (Player) event.getEntityLiving();
        Player source = (Player) event.getSource().getDirectEntity();

        if (PartyHelper.Server.GetRelation((ServerPlayer) player, (ServerPlayer) source) == EnumRelation.PARTY) {
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent
    public void OnPlayerMove(TickEvent.PlayerTickEvent event)
    {
        if (event.player.getCommandSenderWorld().isClientSide) // Don't care if it's a client event.
            return;

        Player player = event.player;
        PlayerStats stats = MMOParties.GetStatsByName(player.getName().getContents());

        stats.TickTeleport();
        if (stats.party != null) MMOParties.PlayerStats.get(player).party.SendPartyMemberData(player, false); // Sync the player.
    }
}
