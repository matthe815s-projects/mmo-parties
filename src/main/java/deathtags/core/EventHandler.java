package deathtags.core;

import deathtags.config.ConfigHolder;
import deathtags.stats.PlayerStats;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class EventHandler {
	
	@SubscribeEvent
	public void onPlayerJoined(PlayerLoggedInEvent event)
	{
		Player player = event.getPlayer();
		
		if (!MMOParties.PlayerStats.containsKey(player))
			MMOParties.PlayerStats.put(player, new PlayerStats ( player ));
	}

	@SubscribeEvent
	public void disconnectFromServer(ClientPlayerNetworkEvent.LoggedOutEvent event)
	{
		MMOParties.localParty = null;
	}
	
	@SubscribeEvent
	public void onPlayerLeave(PlayerLoggedOutEvent event)
	{
		if (MMOParties.GetStatsByName(event.getPlayer().getName().getContents()) == null)
			return;
		
		PlayerStats playerStats = MMOParties.GetStatsByName( event.getPlayer().getName().getContents() );
		Player player = event.getPlayer();
		
		// Leave a party if you're currently in one.
		if ( playerStats.InParty() ) playerStats.party.Leave(player);
		
		MMOParties.PlayerStats.remove(player);
	}

	@SubscribeEvent(priority=EventPriority.HIGHEST)
	public void OnPlayerHurt(LivingHurtEvent event)
	{
		if (event.getEntity().getCommandSenderWorld().isClientSide) return; // Perform on the server only.
		if (! (event.getEntityLiving() instanceof Player) || ! (event.getSource().getDirectEntity() instanceof Player) ) return; // Only perform on players.
		
		Player player = (Player) event.getEntityLiving();
		Player source = (Player) event.getSource().getDirectEntity();
		
		PlayerStats playerStats = MMOParties.GetStatsByName( player.getName().getContents() );
		PlayerStats sourceStats = MMOParties.GetStatsByName( source.getName().getContents() );

		if (!ConfigHolder.COMMON.friendlyFireDisabled.get()) return; // Friendly fire is allowed so this doesn't matter.
		if (!playerStats.InParty() || !sourceStats.InParty()) return; // Nothing matters if both players aren't in a party.
		if (playerStats.party.IsMember( source )) { event.setCanceled(true); return; } // Cancel received damage if it's from a party member.
		
		if (playerStats.party.opposer == null) {
			playerStats.party.opposer = sourceStats.party;
			playerStats.party.opposer.opposer = playerStats.party;
		}
	}
	
	@SubscribeEvent
	public void OnPlayerMove(PlayerTickEvent event)
	{
		if (event.player.getCommandSenderWorld().isClientSide)
			return;
		
		Player player = (Player)event.player;
		PlayerStats stats = MMOParties.GetStatsByName(player.getName().getContents());
		
		// Process teleporting.
		if (stats.teleportTicks > 0) {
			stats.teleportTicks --;
			
			if (stats.teleportTicks <= 0) stats.CommenceTeleport(); // Teleport the player.
		}

		if (stats.party != null) MMOParties.PlayerStats.get(player).party.SendPartyMemberData(player, false); // Sync the player.
	}
}
