package deathtags.core.events;

import deathtags.api.PartyHelper;
import deathtags.api.relation.EnumRelation;
import deathtags.core.ConfigHandler;
import deathtags.core.MMOParties;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class EventCommon {
    public static Party globalParty = null;

    @SubscribeEvent
    public void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        if (!MMOParties.PlayerStats.containsKey(player)) MMOParties.PlayerStats.put(player, new PlayerStats( player ));

        event.player.getServer().getPlayerList().getPlayers().forEach(serverEntityPlayer -> {
            PlayerStats ply = MMOParties.GetStats(serverEntityPlayer);
            if (ply.InParty() && !MMOParties.GetStats(player).InParty()) { // Check if in party
                if (ply.party.IsMemberOffline(player)) { // Check if new player was last in that party
                    ply.party.Join(player, false);
                    return;
                }
            }
        });

        // Handle auto-partying
        if (!ConfigHandler.Server_Options.autoAssignParties) return;

        if (globalParty == null) globalParty = Party.CreateGlobalParty( player );
        else {
            // Don't double join.
            if (!globalParty.IsMember(player)) globalParty.Join(event.player, true);
        }
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        PlayerStats playerStats = MMOParties.GetStatsByName(event.player.getName());

        // Only if in party.
        if (playerStats.InParty()) {
            playerStats.party.players.remove(event.player);

            // Change the leader when you leave.
            if (playerStats.party.players.size() > 0) playerStats.party.MakeLeader(playerStats.party.players.get(0));
        }

        MMOParties.PlayerStats.remove(event.player); // Remove the player's temporary data.
    }

    @SubscribeEvent(priority= EventPriority.HIGHEST)
    public void OnPlayerHurt(LivingHurtEvent event)
    {
        if (!ConfigHandler.Server_Options.friendlyFireDisabled) return; // Friendly fire is allowed so this doesn't matter.
        if (!event.getEntity().world.isRemote) return; // Perform on the server only.

        if (! (event.getEntityLiving() instanceof EntityPlayer)
                || ! (event.getSource().getTrueSource() instanceof EntityPlayer) ) return; // Only perform on players.

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        EntityPlayer source = (EntityPlayer) event.getSource().getTrueSource();

        // Handle friendly fire canceling.
        if (PartyHelper.Server.GetRelation((EntityPlayerMP) player, (EntityPlayerMP) source) == EnumRelation.PARTY) {
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent
    public void OnPlayerMove(TickEvent.PlayerTickEvent event)
    {
        if (!event.player.world.isRemote) // Don't care if it's a client event.
            return;

        EntityPlayer player = event.player;
        PlayerStats stats = MMOParties.GetStatsByName(player.getName());

        if (stats == null) return;

        stats.TickTeleport();
        if (stats.party != null) MMOParties.PlayerStats.get(player).party.SendPartyMemberData(player, false); // Sync the player.
    }
}
