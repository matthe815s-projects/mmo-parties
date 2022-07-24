package deathtags.core.events;

import deathtags.api.PartyHelper;
import deathtags.api.relation.EnumRelation;
import deathtags.core.ConfigHandler;
import deathtags.core.MMOParties;
import deathtags.stats.PlayerStats;
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
    @SubscribeEvent
    public void onPlayerJoined(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        if (!MMOParties.PlayerStats.containsKey(player)) MMOParties.PlayerStats.put((EntityPlayerMP) player, new PlayerStats((EntityPlayerMP) player));

        event.player.getServer().getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
            PlayerStats ply = MMOParties.GetStats(serverPlayerEntity);
            if (ply.InParty() && !MMOParties.GetStats(player).InParty()) { // Check if in party
                if (ply.party.IsMemberOffline(player)) { // Check if new player was last in that party
                    ply.party.Join((EntityPlayerMP) player);
                    return;
                }
            }
        });
    }

    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        PlayerStats playerStats = MMOParties.GetStatsByName(event.player.getName());

        // Only if in party.
        if (playerStats.InParty()) playerStats.party.players.remove(event.player);

        MMOParties.PlayerStats.remove(event.player); // Remove the player's temporary data.
    }

    @SubscribeEvent(priority= EventPriority.HIGHEST)
    public void OnPlayerHurt(LivingHurtEvent event)
    {
        if (!ConfigHandler.Server_Options.friendlyFireDisabled) return; // Friendly fire is allowed so this doesn't matter.
        if (event.getEntity().world.isRemote) return; // Perform on the server only.

        if (! (event.getEntityLiving() instanceof EntityPlayer)
                || ! (event.getSource().getTrueSource() instanceof EntityPlayer) ) return; // Only perform on players.

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        EntityPlayer source = (EntityPlayer) event.getSource().getTrueSource();

        if (PartyHelper.Server.GetRelation((EntityPlayerMP) player, (EntityPlayerMP) source) == EnumRelation.PARTY) {
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent
    public void OnPlayerMove(TickEvent.PlayerTickEvent event)
    {
        if (event.player.world.isRemote) // Don't care if it's a client event.
            return;

        EntityPlayer player = event.player;
        PlayerStats stats = MMOParties.GetStatsByName(player.getName());

        stats.TickTeleport();
        if (stats.party != null) MMOParties.PlayerStats.get(player).party.SendPartyMemberData((EntityPlayerMP) player, false); // Sync the player.
    }
}
