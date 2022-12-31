package deathtags.core.events;

import deathtags.api.PartyHelper;
import deathtags.api.relation.EnumRelation;
import deathtags.config.ConfigHolder;
import deathtags.core.MMOParties;
import deathtags.stats.Party;
import deathtags.stats.PlayerStats;
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
public class EventCommon {
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
        Player player = event.getEntity();
        if (!MMOParties.PlayerStats.containsKey(player)) MMOParties.PlayerStats.put(player, new PlayerStats( player ));

        // Attempt to rejoin the last party in which the player was online in.
        RejoinLastParty(player);

        // Automatically assign the global party to this player.
        // Will not assign if the previous re-join party function goes through.
        if (ConfigHolder.COMMON.autoAssignParties.get())
            HandleGlobalParty(player);
    }

    public void RejoinLastParty(Player player)
    {
        for (ServerPlayer serverPlayer : player.getServer().getPlayerList().getPlayers()) {
            PlayerStats svStats = MMOParties.GetStats(serverPlayer); // Get the stats for this server player.
            if (svStats == null) return;

            // Continue if this is not the party we're looking for.
            if (!svStats.InParty() || !svStats.party.IsMemberOffline(player)) continue;
            // Join the player to this party since it's the one.
            svStats.party.Join(player, false);
            svStats.party.Broadcast(Component.translatable("rpgparties.message.party.player.returned", player.getName().getString()));
            break; // Stop here.
        }
    }

    public void HandleGlobalParty(Player player)
    {
        if (globalParty == null) globalParty = Party.CreateGlobalParty( player );
        else if (!globalParty.IsMember(player)) globalParty.Join(player, false);
    }

    /**
     * Handle a player leaving a server/world.
     * @param event
     */
    @SubscribeEvent
    public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event)
    {
        PlayerStats playerStats = MMOParties.GetStats(event.getEntity());
        if (playerStats == null) return;

        MMOParties.PlayerStats.remove(event.getEntity()); // Remove the player's temporary data.

        // Process the handling for shifting the leader.
        if (!playerStats.InParty()) return;

        playerStats.party.players.remove(event.getEntity());

        playerStats.party.SendUpdate();
        playerStats.party.SendPartyMemberData(event.getEntity(), true, true);

        // Change the leader when you leave.
        if (event.getEntity() == playerStats.party.leader && playerStats.party.players.size() > 0) playerStats.party.MakeLeader(playerStats.party.players.get(0));
    }

    /**
     * Handle canceling PVP damage when player attacking player.
     * @param event
     */
    @SubscribeEvent(priority= EventPriority.HIGHEST)
    public void OnPlayerHurt(LivingHurtEvent event)
    {
        if (!ConfigHolder.COMMON.friendlyFireDisabled.get()) return; // Friendly fire is allowed so this doesn't matter.
        if (event.getEntity().getCommandSenderWorld().isClientSide) return; // Perform on the server only.

        if (! (event.getEntity() instanceof Player || event.getEntity() instanceof Wolf) // Friendly fire preventative measure only apply to players.
                || ! (event.getSource().getDirectEntity() instanceof Player) ) return;

        Player player, source = (Player) event.getSource().getDirectEntity();

        // Determine the owner if the pet if it's a pet.
        if (event.getEntity() instanceof Wolf)
            player = (Player) ((Wolf) event.getEntity()).getOwner();
        else
            player = (Player) event.getEntity();

        if (player == null || MMOParties.GetStats(source).pvpEnabled) return; // If the source has PVP enabled, then skip the rest of the code.

        // Handle friendly fire canceling.
        if ((PartyHelper.Server.GetRelation((ServerPlayer) player, (ServerPlayer) source) == EnumRelation.PARTY)) {
            event.setCanceled(true);
            return;
        }
    }

    /**
     * Process the game ticks, specifically for teleporting and updating the member's information.
     * @param event
     */
    @SubscribeEvent
    public void OnPlayerGameTick(TickEvent.PlayerTickEvent event)
    {
        PlayerStats stats = MMOParties.GetStats(event.player);
        if (stats == null) return; // Dunno why there wouldn't be a stats but Minecraft Forge is weird.

        stats.TickTeleport(); // Tick a teleport step.

        if (stats.party != null) stats.party.SendPartyMemberData(event.player, false, false); // Sync the player.
    }
}
