package deathtags.core.events;

import deathtags.api.PartyHelper;
import deathtags.api.relation.EnumRelation;
import deathtags.config.ConfigHolder;
import deathtags.connectors.ProjectMMOConnector;
import deathtags.core.MMOParties;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import harmonised.pmmo.pmmo_saved_data.PmmoSavedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EventCommon {
    public static Party globalParty = null;

    @SubscribeEvent
    public void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event)
    {
        PlayerEntity player = event.getPlayer();
        if (!MMOParties.PlayerStats.containsKey(player)) MMOParties.PlayerStats.put(player, new PlayerStats( player ));

        event.getPlayer().getServer().getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
            PlayerStats ply = MMOParties.GetStats(serverPlayerEntity);
            if (ply.InParty() && !MMOParties.GetStats(player).InParty()) { // Check if in party
                if (ply.party.IsMemberOffline(player)) { // Check if new player was last in that party
                    ply.party.Join(player);
                    return;
                }
            }
        });

        // Handle auto-partying
        if (!ConfigHolder.COMMON.autoAssignParties.get()) return;

        if (globalParty == null) globalParty = new Party(event.getPlayer());
        else globalParty.Join(event.getPlayer());
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        PlayerStats playerStats = MMOParties.GetStatsByName(event.getPlayer().getName().getString());

        // Only if in party.
        if (playerStats.InParty()) {
            playerStats.party.players.remove(event.getPlayer());

            // Change the leader when you leave.
            if (playerStats.party.players.size() > 0) playerStats.party.MakeLeader(playerStats.party.players.get(0));
        }

        MMOParties.PlayerStats.remove(event.getPlayer()); // Remove the player's temporary data.
    }

    @SubscribeEvent(priority= EventPriority.HIGHEST)
    public void OnPlayerHurt(LivingHurtEvent event)
    {
        if (!ConfigHolder.COMMON.friendlyFireDisabled.get()) return; // Friendly fire is allowed so this doesn't matter.
        if (event.getEntity().getCommandSenderWorld().isClientSide) return; // Perform on the server only.

        if (! (event.getEntityLiving() instanceof PlayerEntity)
                || ! (event.getSource().getDirectEntity() instanceof PlayerEntity) ) return; // Only perform on players.

        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        PlayerEntity source = (PlayerEntity) event.getSource().getDirectEntity();

        if (PartyHelper.Server.GetRelation((ServerPlayerEntity) player, (ServerPlayerEntity) source) == EnumRelation.PARTY) {
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent
    public void OnPlayerMove(TickEvent.PlayerTickEvent event)
    {
        if (event.player.getCommandSenderWorld().isClientSide) // Don't care if it's a client event.
            return;

        PlayerEntity player = event.player;
        PlayerStats stats = MMOParties.GetStatsByName(player.getName().getContents());

        if (stats == null) return;

        // Project MMO compatability
        if (ProjectMMOConnector.IsLoaded() // If the project MMO mod is installed, and you're in a party, automatically party you together.
                && PmmoSavedData.get().getParty(event.player.getUUID()) != null && ! stats.InParty() ) {
            ProjectMMOConnector.JoinParty((ServerPlayerEntity) event.player);
        }

        stats.TickTeleport();
        if (stats.party != null) MMOParties.PlayerStats.get(player).party.SendPartyMemberData(player, false); // Sync the player.
    }
}
