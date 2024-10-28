package dev.matthe815.mmoparties.common.events;

import dev.matthe815.mmoparties.forge.api.PartyHelper;
import dev.matthe815.mmoparties.forge.api.relation.EnumRelation;
import dev.matthe815.mmoparties.forge.config.ConfigHolder;
import dev.matthe815.mmoparties.forge.core.MMOParties;
import dev.matthe815.mmoparties.common.stats.Party;
import dev.matthe815.mmoparties.common.stats.PlayerStats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;

public class EventCommon {
    /**
     * Global player instance, gets filled when the first player joins a server with global parties enabled.
     * All players that join after will be put into this party.
     */
    public static Party globalParty = null;

    /**
     * Handle a player joining a server/world.
     * @param player
     */
    public static void onPlayerJoined(EntityPlayer player)
    {
        if (!MMOParties.PlayerStats.containsKey(player)) MMOParties.PlayerStats.put(player, new PlayerStats( player ));

        // Attempt to rejoin the last party in which the player was online in.
        RejoinLastParty(player);

        // Automatically assign the global party to this player.
        // Will not assign if the previous re-join party function goes through.
        if (ConfigHolder.COMMON.autoAssignParties)
            HandleGlobalParty(player);
    }

    public static void RejoinLastParty(EntityPlayer player)
    {
        for (EntityPlayerMP serverPlayer : player.getServer().getPlayerList().getPlayers()) {
            PlayerStats svStats = MMOParties.GetStatsByName(serverPlayer.getName()); // Get the stats for this server player.

            // Continue if this is not the party we're looking for.
            if (!svStats.InParty() || !svStats.party.IsMemberOffline(player)) continue;
            // Join the player to this party since it's the one.
            svStats.party.Join(player, false);
            svStats.party.Broadcast(new TextComponentTranslation("rpgparties.message.party.player.returned", player.getName()));
            break; // Stop here.
        }
    }

    public static void HandleGlobalParty(EntityPlayer player)
    {
        if (globalParty == null) globalParty = Party.CreateGlobalParty( player );
        else if (!globalParty.IsMember(player)) globalParty.Join(player, false);
    }

    /**
     * Handle a player leaving a server/world.
     * @param player
     */
    public static void onPlayerLeave(EntityPlayer player)
    {
        PlayerStats playerStats = MMOParties.GetStats(player);
        if (playerStats == null) return;

        // Process the handling for shifting the leader.
        if (playerStats.InParty())
        {
            playerStats.party.players.remove(player);

            playerStats.party.SendUpdate();
            playerStats.party.SendPartyMemberData(player, true, true);

            // Change the leader when you leave.
            if (player == playerStats.party.leader && !playerStats.party.players.isEmpty()) playerStats.party.MakeLeader(playerStats.party.players.get(0));
        }

        MMOParties.PlayerStats.remove(player); // Remove the player's temporary data.
    }

    /**
     * Handle canceling PVP damage when player attacking player.
     * @param entity
     */
    public static boolean OnPlayerHurt(Entity entity, Entity damageSource)
    {
        if (!ConfigHolder.COMMON.friendlyFireDisabled) return false; // Friendly fire is allowed so this doesn't matter.
        if (!entity.getEntityWorld().isRemote) return false; // Perform on the server only.
        if (!(entity instanceof EntityPlayer || entity instanceof EntityWolf) || !(damageSource instanceof EntityPlayer)) return false;

        EntityPlayer player;

        // Determine the owner if the pet if it's a pet.
        if (entity instanceof EntityWolf)
            player = (EntityPlayer) ((EntityWolf) entity).getOwner();
        else
            player = (EntityPlayer) entity;

        if (player == null || MMOParties.GetStats((EntityPlayer) damageSource).pvpEnabled) return false; // If the source has PVP enabled, then skip the rest of the code.

        // Handle friendly fire canceling.
        if ((PartyHelper.Server.GetRelation((EntityPlayerMP) player, (EntityPlayerMP) damageSource) == EnumRelation.PARTY)) {
            return true;
        }
        return false;
    }

    /**
     * Process the game ticks, specifically for teleporting and updating the member's information.
     * @param player
     */
    public static void OnPlayerGameTick(EntityPlayer player)
    {
        PlayerStats stats = MMOParties.GetStats(player);
        if (stats == null) return; // Don't know why there wouldn't be a stats but Minecraft Forge is weird.

        stats.TickTeleport(); // Tick a teleport step.

        if (stats.party != null) stats.party.SendPartyMemberData(player, false, false); // Sync the player.
    }
}
