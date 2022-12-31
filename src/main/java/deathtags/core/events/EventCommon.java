package deathtags.core.events;

import deathtags.api.PartyHelper;
import deathtags.api.relation.EnumRelation;
import deathtags.core.ConfigHandler;
import deathtags.core.MMOParties;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
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

        // Attempt to rejoin the last party in which the player was online in.
        RejoinLastParty(player);

        // Automatically assign the global party to this player.
        // Will not assign if the previous re-join party function goes through.
        if (ConfigHandler.Server_Options.autoAssignParties)
            HandleGlobalParty(player);
    }

    public void RejoinLastParty(EntityPlayer player)
    {
        for (EntityPlayerMP serverPlayer : player.getServer().getPlayerList().getPlayers()) {
            PlayerStats svStats = MMOParties.GetStats(serverPlayer); // Get the stats for this server player.
            // Continue if this is not the party we're looking for.
            if (!svStats.InParty() || !svStats.party.IsMemberOffline(player)) continue;
            // Join the player to this party since it's the one.
            svStats.party.Join(player, false);
            svStats.party.Broadcast(new TextComponentTranslation("rpgparties.message.party.player.returned", player.getName()));
            break; // Stop here.
        }
    }

    public void HandleGlobalParty(EntityPlayer player)
    {
        if (globalParty == null) globalParty = Party.CreateGlobalParty( player );
        else if (!globalParty.IsMember(player)) globalParty.Join(player, false);
    }


    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        PlayerStats playerStats = MMOParties.GetStats(event.player);
        MMOParties.PlayerStats.remove(event.player); // Remove the player's temporary data.

        // Process the handling for shifting the leader.
        if (!playerStats.InParty()) return;

        playerStats.party.players.remove(event.player);

        playerStats.party.SendUpdate();
        playerStats.party.SendPartyMemberData(event.player, true, true);

        // Change the leader when you leave.
        if (event.player == playerStats.party.leader && playerStats.party.players.size() > 0) playerStats.party.MakeLeader(playerStats.party.players.get(0));
    }

    @SubscribeEvent(priority= EventPriority.HIGHEST)
    public void OnPlayerHurt(LivingHurtEvent event)
    {
        if (!ConfigHandler.Server_Options.friendlyFireDisabled) return; // Friendly fire is allowed so this doesn't matter.
        if (event.getEntity().world.isRemote) return; // Perform on the server only.

        if (! (event.getEntityLiving() instanceof EntityPlayer || event.getEntityLiving() instanceof EntityWolf) // Friendly fire preventative measure only apply to players.
                || ! (event.getSource().getTrueSource() instanceof EntityPlayer) ) return;

        EntityPlayer player, source = (EntityPlayer) event.getSource().getTrueSource();

        // Determine the owner if the pet if it's a pet.
        if (event.getEntityLiving() instanceof EntityWolf)
            player = (EntityPlayer) ((EntityWolf) event.getEntityLiving()).getOwner();
        else
            player = (EntityPlayer) event.getEntityLiving();

        if (player == null || MMOParties.GetStats(source).pvpEnabled) return; // If the source has PVP enabled, then skip the rest of the code.

        // Handle friendly fire canceling.
        if ((PartyHelper.Server.GetRelation((EntityPlayerMP) player, (EntityPlayerMP) source) == EnumRelation.PARTY)) {
            event.setCanceled(true);
            return;
        }
    }

    @SubscribeEvent
    public void OnPlayerGameTick(TickEvent.PlayerTickEvent event)
    {
        PlayerStats stats = MMOParties.GetStats(event.player);
        if (stats == null) return; // Dunno why there wouldn't be a stats but Minecraft Forge is weird.

        stats.TickTeleport();

        if (stats.party != null) stats.party.SendPartyMemberData(event.player, false, false); // Sync the player.
    }
}
